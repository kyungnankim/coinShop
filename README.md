# coinShop!

mysql database shop으로 생성
user : root
password : 1234

create database shop default character set utf8 collate utf8_general_ci;

유저

로그인 : root@1234 비밀번호 : root@1234

회원가입 후 로그인하여 코인을 구매할 수 있게 해줍니다.

maven에서 gradle 라이브러리 변경해서 적용했습니다. Gradle이 Maven보다 설정 내용이 짧아지고 가독성이 좋으며, 의존관계가 복잡한 프로젝트에도 설정하기에 적절하고 상속구조를 이용한 멀티 모듈 구현할 수 있다는 점에 변경해서 적용했습니다.

구성도 
CartItemDto.java - Cart Class에서 회원 엔티티를 파라미터로 받아 장바구니 엔티티 생성하는 로직 추가
Cart.java - 장바구니에 담을 상품 엔티티를 생성하는 메소드와 장바구니에 담을 수량을 증가시켜 주는 메소드를 CartItem클래스에 추가
CartItem.java - 현재 로그인한 회원의 Cart 엔티티를 찾기 위해 CartRepository에 쿼리 메소드 추가
CartRepository.java - 장바구니에 상품을 담는 로직을 작성하기위해 com.kyungnan.shop.service 패키지 아래에 cartService 클래스 생성
CartController.java CarService.java 장바구니와 관련된 요청들을 처리하기위해 com.kyungnan.shop.controller 패키지 아래에 CartController 클래스 생성

트러블슈팅
1.docker 에서 mysql 문제
ERROR] [Entrypoint]: MYSQL_USER="root", MYSQL_PASSWORD cannot be used for the root user
    Use one of the following to control the root user password:
    - MYSQL_ROOT_PASSWORD
    - MYSQL_ALLOW_EMPTY_PASSWORD
    - MYSQL_RANDOM_ROOT_PASSWORD

이것의 요점 중 일부는 MYSQL_USER=root가 중복된다는 것입니다. 항상 'root'@'localhost'가 있고 기본적으로 'root'@'%'가 있으며 여기에는 MYSQL_ROOT_PASSWORD(또는 MYSQL_ALLOW_EMPTY_PASSWORD)로 설정된 비밀번호가 있다하여 유저명 변경 후 도커 실행하니 정상적으로 작동했습니다.

2. DBeaver  MYSQL 접근권한
docker ps -a
docker exec -i -t mysql_container bash 접속 후
docker exec -i -t mysql_shop bash
root@d2ff3dd67445:/# mysql -u root -p
로그인 시도
password : 1234
로그인 완료!
mysql > grant all privileges on *.* to 'root'@'%'; 모든 접근권한 허용
mysql> SHOW DATABASES;
특별히 
create user '사용자 이름'@'ip주소' identified by '비밀번호';
로 특정 사용자를 추가해서 권한과 접근 위치 세분화 가능합니다


3. JPA 엔터티 테이블 생성
테이블 생성의 경우 브랜치를 이동하면 엔티티와 테이블이 맞지 않아서 제대로 동작하지 않는 경우가 있어,
mysql workbench로 접속해 
drop database shop; 
create database shop default character set utf8 collate utf8_general_ci; 명령어를 이용해서
shop database를 한번 지우고 다시 만들어서 애플리케이션을 실행했습니다.



swagger : http://localhost:8080/swagger-ui/index.html
![174814259-39752797-33ed-4854-ad33-17ee34e55263](https://user-images.githubusercontent.com/72008368/174918167-6ab27406-a7d0-463b-844f-08d752b39ef4.png)
![174814280-118d93c7-6b02-47e1-b4df-2f5b2ce0db1e](https://user-images.githubusercontent.com/72008368/174918170-5a158339-84ed-491e-8818-70170fcbea52.png)
![174814339-1de5c80f-055e-4ff5-b6de-941be298ad27](https://user-images.githubusercontent.com/72008368/174918171-7a432aed-0463-4532-8705-8fe5af93c8b3.png)
![174814387-a048b54f-cd1e-4eac-a004-89d06b5a098c](https://user-images.githubusercontent.com/72008368/174918174-40fcd982-6318-4d05-9ad7-0abe11367fe4.png)
![174814470-7802584b-79fa-4be8-a4cd-95e5a26d74bc](https://user-images.githubusercontent.com/72008368/174918179-d29a7fa7-9912-453c-a77f-731767ae628e.png)
![174814505-dfef88cd-397a-4182-b1e7-862dc2220d34](https://user-images.githubusercontent.com/72008368/174918180-3396e050-b825-426e-b88a-b377fdb94d98.png)




