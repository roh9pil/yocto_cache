# Yocto Cache

Recipe의 `SRC_URI` 변수에는 소스코드르 어디에서 얻을 수 있는지 저장되어 있다. 
우리는 이것을 upstream URI 라고도 부른다.

그런데, upstream URI 에는 항상 접근 가능하지 않을 수도 있다.
따라서, 가용성을 위해서 Yocto는 Mirror 매커니즘을 제공한다.

Yocto 빌드 과정 중에 소스코드를 가져오는 것은 BitBake 에서 진행한다.
아래에서는 BitBake가 베이킹할때 Recipe의 재료 (SRC_URI)를 확보하기 위해서
mirror를 어떻게 사용하는지 알아보기로 한다.

## BitBake 에서 베이킹할때 재료가 없을 경우에는?
 
