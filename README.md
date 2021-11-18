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

그래서, PREMIRROR, MIRROR 값을 항상 연결 가능한 곳으로 설정해주면 upstream URI엣 다운로드가 불가능한 경우에도 소스 코드를 받을 수 있다.
그런데 PREMIRROR, MIRROR 값을 어떻게 적어야 하는 걸까?
문법애 대한 이해가 필요하다.

## Syntax: SRC_URI를 mirror site용으로 치환
BitBake에서는 SRC_URI를 정규 표현식으로 표현된 패턴을 찾아서 지정된 값으로 치환하는 방법으로 mirror site용으로 변환한다.
변환된 uri를 사용해서 mirror site로부터 소스코드를 다운로드 받는다. 
Upstream URI의 형식과 URI 각 부분의 패턴, 그리고 치환될 문자열의 표현 방법을 순서대로 알아본다. 

### SRC_URI

`scheme`://`user`:`password`@`hostname`/`pass`;`parameters`

### Patterns

### Replacements





