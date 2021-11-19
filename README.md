# Yocto Cache

Recipe의 `SRC_URI` 변수에는 소스코드를 어디에서 얻을 수 있는 장소가 저장되어 있다. 
우리는 이것을 upstream URI 라고도 부른다.

그런데, upstream URI 에는 항상 접근 가능하지 않을 수도 있다.
가용성을 위해서 Yocto는 Mirror 매커니즘을 제공한다.

Yocto 빌드 과정 중에 소스코드를 가져오는 것은 BitBake 에서 진행한다.
아래에서는 BitBake가 베이킹할때 Recipe의 재료 (SRC_URI)를 확보하기 위해서
mirror를 어떻게 사용하는지 알아보기로 한다.

## 베이킹하려고 하는데 밀가루가 필요하다.
1. 우리집 냉장고나 다용도실에서 전에 사놓은 밀가루가 있는지 찾아본다.
2. 집에는 없으니 인터넷으로 주문한다.
3. 인터넷이 안되거나 품절이다, 옆집에 밀가루가 있다면 빌려쓰자.
4. (좀 특수한 상황으로) 어제 쓰고 남은 반죽이 있다.

BitBake에서 Source mirror를 사용하는 것은 위와 같다. 
* `PREMIRROR`에서 찾아본다 (1번). 
* 실패하면 `SRC_URI`에 접속해서 가져온다 (2번). 
* upstream uri에 접근이 안되면 (일시적인 네트웍 문제 등등) `MIRROR`에서 찾는다.
* 위는 Source Code (밀가루) 를 확보하는 과정이고, Yocto에서는 prebuilt object를 위해서 `SSTATE_MIRRORS`를 지원한다.

그래서, PREMIRROR, MIRROR 값을 항상 연결 가능한 곳으로 설정해주면 upstream URI에서 다운로드가 불가능한 경우에도 소스 코드를 받을 수 있다.
그런데 PREMIRROR, MIRROR 값을 어떻게 적어야 하는 걸까?
문법에 대한 이해가 필요하다.

## Syntax: SRC_URI를 mirror site용으로 치환

```python
PREMIRROR_prepend = "\
                    `PATTERN1` `REPLACEMENT1` \n \
                    `PATTERN2` `REPLACEMENT2` \n"
```

BitBake에서는 SRC_URI를 정규 표현식으로 표현된 패턴을 찾아서 매칭된 부분을 지정된 값으로 치환하는 방법으로 mirror site용으로 변환한다.
변환된 uri를 사용해서 mirror site로부터 소스코드를 다운로드 받는다. 
Upstream URI의 형식과 URI 각 부분의 패턴, 그리고 치환될 문자열의 표현 방법을 순서대로 알아본다. 

### SRC_URI

`scheme`://`user`:`password`@`hostname`/`path`;`parameters`

### Patterns
parameters를 제외하고 나머지는 모두 정규 표현식으로 패턴을 지정한다.
* scheme의 경우에는 뒤에 $를 붙여서 매칭을 시도하기 때문에 http 라고 적으면, BitBake가 http$ 로 매칭을 시도하기 때문에 https를 사용하는 SRC_URI는 매칭되지 않는다.
* 예) git protocol, main branch를 사용하는 모든 source에 매칭되는 패턴
```python
git://.*/.*;branch=main
```

### Replacements
미리 정의된 문자열이 있다. Replacement에서 이 문자열이 보이면 SRC_URI에서 매칭된 문자열을 사용하겠다는 의미이다.
* TYPE: SRC_URI의 `scheme`
* HOST: SRC_URI의 `hostname`
* PATH: SRC_URI의 `path`
* BASENAME: SRC_URI의 `path` 에서 마지막 '/' 뒤에 따라오는 문자열
* MIRRORNAME: HOST와 PATH를 붙여서 만든 문자열로, ':', '/', '*'은 '.'로 치환된다.

파라미터는 조금 다르게 처리되는데, 먼저 scheme이 달라지면 파라미터가 의미가 없기 때문에 파라미터는 무시된다.
scheme이 변하지 않았다며 결과는 SRC_URI에 있던 파라미터들과 Replacements에 있는 파라미터의 합집합이다.
이때, 동일한 파라미터가 있다면 그 value는 replacement의 것을 사용한다.

마지막으로 path를 한번 더 점검하고 path뒤에 추가 정보를 붙여서 완성한다. 
치환된 path 문자열이 SRC_URI의 basename으로 끝나지 않을때 아래와 같이 규칙을 적용한다.
* scheme이 바뀌지 않았다면 basename은 SRC_URI path의 basename을 추가로 붙인다.
  - 파일명 하나하나를 치환하는 값으로 적어두는 것은 너무 번거로운 일이니까
* scheme이 바뀌었을때
  - SRC_URI가 하나의 파일 (tarball 포함) 가리키고 있었다면, SRC_URI path의 basename을 뒤에 붙여준다.
  - SRC_URI가 repository 였다면, mirrortarball 이름을 추가로 붙이는데 이건 scheme 마다 다르다.  
    - git의 경우  git2_hostname.path.to.repo.git.tar.gz

## 예제
```python
SRC_URI = "git://git.yoctoproject.org/foo/myutils.git;branch=master;tag=123456789"
PREMIRRORS = "git://.*/.*;branch=master http://myserver.org/cache/ \n"
```
1. Upstream
     - scheme: "git"
     - user: ""
     - password: ""
     - host: "git.yoctoproject.org"
     - path: "foo/myutils.git"
     - parameters: {"branch": "master", "tag": "123456789"}
2. Pattern
     - scheme: "git"
     - user: ""
     - password: ""
     - host: ".*"
     - path: ".*"
     - parameters: {"branch": "master"}
3. replacement
     - scheme: "git"
     - user: ""
     - password: ""
     - host: "myserver.org"
     - path: "cache/"
     - parameters: {}


### 치환 결과
- scheme: "http"
- user: ""
- password: ""
- host: "myserver.org"
- path: "cache/git2_git.yoctoproject.org.foo.myutils.git.tar.gz"
- parameters: {}

`git2_git.yoctoproject.org.foo.myutils.git.tar.gz` 는 마지막에 추가

각 부분의 변환 결과를 조합하여 mirror site용 uri를 생성했다.
주어진 SRC_URI와 PREMIRROR (pattern replacement) 를 함께 보면 아래와 같다.

```python
SRC_URI = "git://git.yoctoproject.org/foo/myutils.git;branch=master;tag=123456789"
PREMIRRORS = "git://.*/.*;branch=master http://myserver.org/cache/ \n"
```
변환 결과
```python
http://myserver.org/cache/git2_git.yoctoproject.org.foo.myutils.git.tar.gz
```
## 왜 PREMIRROR, MIRROR 두 개가 있을까?
이런 궁금증이 있었으나, 아직 공식 레퍼런스 메뉴얼에서는 찾지 못했다.
아마도 3 strikes == 1 out 이랑 관련이 있지 않을까? 
SRC_URI가 접근이 안될 경우를 대비해서 두번의 기회를 더 주는 것이니까..

## Reference
* yocto project reference manual: https://yoctoproject.org/docs/current/ref-manial/ref-manual.html
* yocto project mega manual: https://yoctoproject.org/docs/current/mega-manual/mega-manual.html
* bitbake user manual: https://yoctoproject.org/docs/current/bitbake-user-manual/bitbake-user-manual
