### 네이버 지식인

**구현 기능**
1. 게시글 조회
2. 게시글에 사진과 함께 글, 해시태그 작성하기 
3. 게시글에 댓글 및 대댓글 기능
4. 게시글 댓글에 좋아요, 싫어요 기능
5. 게시글, 댓글, 좋아요 삭제 기능



#### 네이버 지식인 구조
1. 질문 (Post)
<img src="https://github.com/user-attachments/assets/7c03d81a-2dc6-4525-a813-80c91426f3e0" width="50%">

2. 답변 (Answer)
<img src="https://github.com/user-attachments/assets/a648fd62-186e-49f9-a412-3beb63e4be2b" width="50%">

4. 좋아요/싫어요 (Like_dislike) + 댓글 (Comment)
<img src="https://github.com/user-attachments/assets/251a48a4-0e74-4030-bde4-0b8d74e4e200" width="50%">



## Mission 1️⃣ 데이터 모델링 
(1) **ERD**![Image](https://github.com/user-attachments/assets/e1c66816-b435-4335-80f9-a36cbd603e03)**1. User**
- 한 명의 user은 여러개의 **Post, Aswer, like_dislike, comment**를 작성 가능 (User와 1:N 관계)

**2. Post**
- 하나의 Post에는 여러개의 **Comment, Answer, like_dislike, image** 작성 가능 (Post와 1:N 관계)
- Post와 **Hashtag**는 N:M 관계 -> 중간에 PostHash table 설정

**3. Answer**
- 하나의 Answer에는 여러개의 **comment, Image, like_dislike** 작성 가능 (Answer과 1:N 관계)

### (2) Entity 설계 

**1. LikeDislike**
```
@Enumerated(EnumType.STRING)
   private LikeStatus likestatus;

@Enumerated(EnumType.STRING)
    private TargetStatus targetstatus;
```
- 이 부분에서, LikeStatus는 Enum으로 관리하여 Like, Dislike 설정
- 좋아요/싫어요는 Post와 Answer에 달 수 있으므로 TargetStatus에서 Post, Answer로 관리

**2. Comment**
```
  @Enumerated(EnumType.STRING)
    private TargetStatus targetStatus;
```
- Comment 또한 Post와 Answer에 각각 작성 가능하므로 TargetStatus를 이용하여 하나의 테이블에서 관리

🌟 Comment는 갯수가 많지 않을 것 같고 코드 중복을 피하려고 이 방식을 사용했는데 Post_Comment와 Answer_Comment로 나누는게 나을까요? 의견 부탁드립니다 🌟



## Mission 2️⃣ Repository 단위 테스트 (Post Entity 사용)

**1. User 생성**
```
@BeforeEach
    public void setUp() {
        // 테스트에 사용할 사용자 데이터 생성
        user = User.builder()
                .nickname("dohyun")
                .email("dohyun@naver.com")
                .password("1234")
                .build();

        userRepository.save(user);
    }
```
<img src="https://github.com/user-attachments/assets/8b2227f0-43b7-4b36-ae46-accc9386423d" width="60%">


**2. 작성자를 기준으로 FindPost**
- 첫번째 Post 생성 
```
@Test
    public void testFindByWriter() {
        // given

        //첫번째 질문글 (사진 X)
        Post post1 = Post.builder()
                .title("Post 1")
                .content("hello")
                .writer(user)
                .build();
        postRepository.save(post1);

```
<img src="https://github.com/user-attachments/assets/dffeab57-e437-48eb-ace8-fc28a72cc7af" width="60%">


- 두번째 Post 생성 
```
 Image image = Image.builder()
                .imageUrl("image.jpg") // 이미지 URL 설정
                .post(null)  // 아직 Post와 연결되지 않음
                .build();

        //2번째 질문글 (사진 1장)
        Post post2 = Post.builder()
                .title("Post 2")
                .content("one picture")
                .images(Collections.singletonList(image))
                .writer(user)
                .build();
        image.setPost(post2);
        postRepository.save(post2);
```
<img src="https://github.com/user-attachments/assets/a45d367d-4a33-4cc7-9504-4db1a22590cb" width="60%">


- 세번째 Post 생성
```
 //3번쨰 질문글 (사진 2장)
        Post post3 = Post.builder()
                .title("Post 3")
                .content("two pictures")
                .images(Arrays.asList())
                .writer(user)
                .build();
        postRepository.save(post3);

        Image image1 = Image.builder()
                .imageUrl("image_url_1")
                .post(post3)
                .build();

        Image image2 = Image.builder()
                .imageUrl("image_url_2")
                .post(post3)
                .build();

        imageRepository.save(image1);
        imageRepository.save(image2);

```
<img src="https://github.com/user-attachments/assets/5e5e52df-bfa7-459e-a5b9-5ecf8d3dd19b" width="60%">

- Post DB
<img src="https://github.com/user-attachments/assets/fe378097-02c1-4153-979f-ea16c396b5f2" width="60%">

- Image DB
 <img src="https://github.com/user-attachments/assets/e5232e8e-39c2-48da-80d2-0445e0744e42" width="60%">

- 나머지 when/then
```
// when
        List<Post> posts = postRepository.findByWriter(user);
// then
        assertThat(posts).hasSize(3);
        assertThat(posts).extracting(Post::getTitle).containsExactly("Post 1", "Post 2","Post 3");
```



## Mission 3️⃣ JPA 관련 문제
### (1) 어떻게 data jpa는 interface만으로도 함수가 구현이 되는가?
```
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByWriter(User writer);
}

```
- Spring이 애플리케이션을 실행하면서 PostRepository의 프록시 객체를 생성

- 인터페이스만 정의하면 Spring이 동적으로 구현체를 만들어 주입
이 때, SimpleJpaRepository 클래스가 작동하며 메서드 이름을 분석해 쿼리 자동 생성

> findByWriter(User writer)
→ "SELECT p FROM Post p WHERE p.writer = ?"

- Spring이 내부적으로 EntityManager를 사용하여 쿼리를 실행하고 결과 반환



### (2)  왜 계속 생성되는 entity manager를 생성자 주입을 이용하는가?
- **EntityManager은 싱글톤 객체가 아니다 !!**
- 트랜잭션이 시작될 때 새로운 EntityManager 객체가 동적으로 생성되며, 트랜잭션이 끝날 때 EntityManager는 폐기됨.

> ❔ **그럼 왜 생성자 주입?**
- EntityManager는 **프록시 객체**로 주입되며, 실제 트랜잭션 범위에서만 EntityManager가 생성되고 관리된다.
- 프록시 객체는 애플리케이션에서 하나의 인스턴스로 관리되며(싱글톤), 필요한 시점에 실제 EntityManager를 동적으로 생성한다.



### (3)  Fetch Join과 Distinct
- **Fetch Join** 이란?
  
 : JPQL에서 성능 최적화를 위해 제공하는 기능
 
 : 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
 
 - **Fetch Join** 사용
 ```
"select t From Team t join fetch t.members where t.name = "팀A";
```

 : Name이 "팀A"인 Team을 조회하면서 해당 팀에 속한 members도  함께 즉시 로딩하여 가져오는 쿼리 (즉시 로딩)
  - 만약 "팀 A"에 **Member가 2명** 있다면?
    : **팀 A가 2번 중복** 됨
   
    
 - 이 때 !! **Distinct**를 사용하면
```
"select distinct t From Team t join fetch t.members where t.name = "팀A";
 ```
 : 중복되었던 "Team A"가 **한번** 만 나오게 된다.
 

 (참고 https://9hyuk9.tistory.com/77)

---
### WEEK 3. ERD 수정
![Image](https://github.com/user-attachments/assets/93f0e2b0-2b97-4426-90e2-4ce2fee3f4cb)
- 좋아요/싫어요는 답변 글에만 달 수 있도록 수정

### 구현 기능
<img src="https://github.com/user-attachments/assets/7954e2c9-b181-4b04-bf30-e042610746bd" width="60%">

- User은 로그인 기능이 아직 없어 임의로 추가했습니다.
  <img src="https://github.com/user-attachments/assets/703d8bc7-4e31-4a0f-a273-7eafaace8ffc" width="70%">

#### 1. 질문 작성
![Image](https://github.com/user-attachments/assets/328be23e-9793-4d8d-b9b9-dae4d0bc77b7)
✨ **여기서 이미지는!! AWS S3 버킷 사용**

<img src="https://github.com/user-attachments/assets/bad6c7b5-cb11-437d-90ef-48e405ef1a10" width="70%">

 - 버킷에 잘 들어갔지요~

#### 2. 내가 쓴 모든 질문글 조회
![Image](https://github.com/user-attachments/assets/9499ae8b-c7ab-40de-b747-7069b8adcc36)

#### 3. 내가 쓴 질문글 삭제
<img src="https://github.com/user-attachments/assets/c51a031a-ad44-409f-a7a6-1a74393c080a" width="70%">

- 삭제 성공~

✨ 삭제하려는 userId와 질문 작성자가 다르면?
![Image](https://github.com/user-attachments/assets/94a62a0a-175b-4577-9cd8-15dbfe3a01b5)
- 에러 발생!!

#### 4. 답변 작성
<img src="https://github.com/user-attachments/assets/51d8f2ab-e820-480a-8766-42f2787c317c" width="60%">

![Image](https://github.com/user-attachments/assets/9b9f714d-7601-438d-8ee8-a8a1d12785de)

✨ 질문 작성자가 답변을 달려 하면?
![Image](https://github.com/user-attachments/assets/7d7b8eac-d01e-4872-8952-f58698339081)
- 에러 발생 !!

#### 5. 질문과 답변 조회
![Image](https://github.com/user-attachments/assets/47a6e657-d499-4a7e-a35f-b204a2ebc45d)
- postId를 PathParameter로 입력하면 그 질문과 답변글들을 조회 가능

#### 6. 좋아요/싫어요 달기
![Image](https://github.com/user-attachments/assets/40f6552c-c7f5-44b2-bb19-26960f1a28a9)
✨ 좋아요/싫어요 연타 방지를 어떻게 할까... 생각하다가 

(1) 좋아요-> 좋아요/ (2) 좋아요-> 싫어요/ (3) 싫어요-> 싫어요/(4) 싫어요->좋아요

모두 에러 처리 나도록 했습니다.

(1) 의 경우

<img src="https://github.com/user-attachments/assets/ec241205-d2d6-4be4-b1d5-0a5f7c42d535" width="70%">

(2),(4)의 경우

<img src="https://github.com/user-attachments/assets/19aee750-7f83-4041-9fc5-8a6c8673d7c2" width="70%">

**결국, LIKE/DISLIKE가 있는 경우, 삭제한 후에만 새로 달 수 있습니다.**

#### 7. 좋아요/싫어요 삭제
<img src="https://github.com/user-attachments/assets/9c3f6c42-b3a9-4dcc-8591-57c919f30b3e" width="50%">

 ***

❔Hashtag를 이용한 질문글 찾기를 위해 HashtagController을 따로 둘지, PostController에 포함시킬지 고민중입니다. 어떻게 하셨나요❔

***
### 부가 구현 설명

**1. ErrorStatus + 성공 응답 처리**
 - exception과 ErrorStatus, SuccessStatus 등을 추가하였습니다. 
 - ErrorStatus에서는 에러 처리를 Custom하여 추가합니다.

**2. Swagger**
- SwaggerConfig를 이용한 Swagger 테스트 설정

**3. Converter**
- DTO <-> Entity 간 변환을 Converter에서 처리
- 서비스 로직의 간결성을 위해

**4. Service + ServiceImpl 사용**
- Service는 인터페이스 구현 + ServiceImpl은 비즈니스 로직 처리
- 확장성을 위해

**5. AWS S3 BUCKET 사용**
- 이미지 업로드를 위해 사용
- MultiPartFile 형식으로 이미지를 S3 버킷에 업로드 후, 이미지 URL을 반환하여 DB에 저장

---
### WEEK 4. 로그인/회원가입 추가 + 이 외 기능 구현

#### 1. 회원가입 + 로그인
**1) 로그인 정보를 받아오기 위한 CustomUserDetails**
``` java
public class CustomUserDetails implements UserDetails {

    private Long userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }
```
이후 **@AuthenticationPrincipal** 로 로그인 정보를 주입받았다.

**2) Spring Security**
```java
    @Bean
public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
            .csrf(AbstractHttpConfigurer::disable) //csrf 비활성화
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(a -> a.requestMatchers("/user/create", "/user/login", "/user/logout", "/connect/**", "/v3/api-docs/**",
                    "/swagger-ui/**", "/swagger-ui.html","permit/**").permitAll().anyRequest().authenticated())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}

@Bean
public PasswordEncoder makePassword() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
}
```
- 로그인/회원가입/스웨거 등은 인증 절차 없이 필터를 통과,
  로그인하지 않은 사용자가 볼 수 있는 화면 (질문+답변 조회) 등은 엔드포인트를 "**permit/**"으로 시작하게 하여 필터 통과
- 비밀번호 암호화를 위한 인코더 생성

**3) JwtAuthFilter**
 ```java
 UserDetails userDetails = new CustomUserDetails(userId, username, null, authorities);

    // Authentication 객체 설정
 Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
```

- JWT 안의 정보로 CustomUserDetails 객체를 만든다.
  이 때, 비밀번호는 이미 토큰으로 인증된 상태이므로 null 처리

- 만든 Authentication을 SecurityContextHolder에 심어 추후 @AuthenticationPrincipal을 통해 로그인 정보를 꺼냄.

#### 2. 로그인 + 비로그인 구분
<img src="https://velog.velcdn.com/images/dohyunii/post/40c0d955-447e-4d68-87d1-83deb398b807/image.png" width="60%" />

- **post를 예로 들면**

  **<내가 쓴 질문 조회/질문 작성/내가 쓴 질문 삭제>** 등의 api는 로그인 정보를 받아와야 하므로 **/post**로 시작함
  <**해시태그별 글 조회**>는 로그인하지 않은 사용자도 조회 가능하므로 **/permit**으로 시작해 필터 통과함

#### 3. 로그아웃 + 엑세스 토큰 재발급
(1) **로그아웃**

- 리프레시 토큰을 레디스에 저장하는 방법도 있다는데 일단 DB에 저장함.
- **RefreshToken** entity 추가
```java
public class RefreshToken {
    @Id
    private Long userId;

    private String refreshToken;
```
- 로그아웃 시, 저장해두었던 사용자의 refreshToken이 삭제되고 재로그인 해야 한다.

(2)**엑세스 토큰 재발급**
- 엑세스 토큰의 유효기간은 30분, 리프레시 토큰의 유효기간은 30일로 설정
- 엑세스 토큰 만료 시, 리프레시 토큰을 이용해 엑세스 토큰을 재발급 받는다.
- 클라이언트가 리프레시 토큰을 요청과 함께 쿠키에서 보내면, 서버에서 이를 검증하여 엑세스 토큰을 갱신한다.

❶ **리프레시 토큰 검증**
```java
RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
        .orElseThrow(() -> new CustomException(ErrorStatus.INVALID_REFRESH_TOKEN));

if (!savedToken.getRefreshToken().equals(refreshToken)) {
    throw new CustomException(ErrorStatus.INVALID_REFRESH_TOKEN);

TokenDTO newTokenDTO = jwtTokenProvider.createToken(user);
}
```
: DB에서 사용자의 리프레시 토큰을 조회하고 비교한 뒤, jwtTokenProvider.createToken(user)를 호출해 새 토큰 발급한다.

```java
  // DB에 리프레시 토큰 업데이트
/ savedToken.setRefreshToken(newTokenDTO.getRefreshToken());

쿠키에 새로운 리프레시 토큰 저장
jwtTokenProvider.setRefreshTokenInCookies(response, newTokenDTO.getRefreshToken());

```
: 발급 받은 새 토큰을 cookie와 db에 업데이트한다.

❷ **JwtTokenProvider**
```java

if (existingToken != null) {
        try {
        // 리프레시 토큰이 유효한지 확인
        Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY)
                        .build()
                        .parseClaimsJws(existingToken.getRefreshToken());

        //유효하면 재사용 (리프레시 토큰은 그대로)
        refreshToken = existingToken.getRefreshToken();
            } catch (ExpiredJwtException e) {
        // 만료된 경우 새로 발급
        refreshToken = createRefreshToken(user);
                existingToken.setRefreshToken(refreshToken);
                refreshTokenRepository.save(existingToken);
            }
                    } 
else {
        refreshToken = createRefreshToken(user);
            refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));
        }

```
- 리프레시 토큰의 만료기한이 남았다면 그대로 반환, 만료기한이 지났다면 새로 발급 받아야한다.
- 리프레시 토큰이 만료된 경우, **재로그인해야 한다는 에러** 터트림.

**실행결과**

<img src="https://velog.velcdn.com/images/dohyunii/post/4d0309e3-5d3c-4b55-93cb-299ce2a8bb1a/image.png" width="60%" />

- 리프레시 토큰 만료 시,
  ![](https://velog.velcdn.com/images/dohyunii/post/d21155e1-2f01-40c0-9d04-2beefd15892c/image.png)


#### 4. 추가 구현 기능
**(1) 회원가입, 로그인**
- 회원가입 시 email, nickname, password 입력
<img src="https://velog.velcdn.com/images/dohyunii/post/39504893-04dd-4dc2-a376-26cd6ba8b9c0/image.png" width="60%" />
- 이후 로그인 시 토큰 반환

  ![](https://velog.velcdn.com/images/dohyunii/post/7362e2c7-ac0d-46c5-9a8a-229122a3ab61/image.png)
- 로그인할 때 리프레시 토큰을 쿠키에 저장
```java
        // 쿠키에 리프레시 토큰 저장
        jwtTokenProvider.setRefreshTokenInCookies(response, tokenDTO.getRefreshToken());
```

**(2) 해시태그별 글 조회**

 <img src="https://velog.velcdn.com/images/dohyunii/post/314325ad-831e-4615-8112-9831b5f53743/image.png" width="40%" /> 
 
 ![](https://velog.velcdn.com/images/dohyunii/post/45c1c7f5-5c5c-446b-9cce-278c02aab72b/image.png)

- **post 삭제 시, post와 hashtag의 관계는 끊고 hashtag는 남겨둠**
``` java
       //4. Post 삭제시 hashtag는 그대로 -> 해당 hashtag의 postId를 null로 설정
        List<PostHash> postHashtags = post.getPostHashtags();
        for (PostHash postHash : postHashtags) {
            postHash.setPost(null);
        }
```
![](https://velog.velcdn.com/images/dohyunii/post/9238ccca-1a0b-4081-a5d0-292bc395f77e/image.png)
: 삭제된 post이기 때문에 post_hash 테이블의 post_id가 null로 바뀌었다.

**(3) 댓글 관련**
- 댓글은 **POST, ANSWER**에 남길 수 있다. 이를 TargetStatus로 구분하였다.
<img src="https://velog.velcdn.com/images/dohyunii/post/71277bc1-08ba-4f2a-ac8e-af22e5dcbb49/image.png" width="50%" />
: TargetStatus에는 POST 또는 ANSWER과 그의 id를 넣으면 된다.


❶ **Post**에 댓글 남김

<img src="https://velog.velcdn.com/images/dohyunii/post/17fb05b4-0d1d-48b4-a8de-8192884fd689/image.png" width="50%" />

❷ **Answer**에 댓글 남김

<img src="https://velog.velcdn.com/images/dohyunii/post/4086b03d-2f08-4aef-a0a1-7c77201a9c88/image.png" width="50%" />

![](https://velog.velcdn.com/images/dohyunii/post/17752005-f2e9-47bb-9d7c-eab40883095e/image.png)

- **Post 삭제 시** 댓글과 답변이 모두 삭제되도록, **Answer만 삭제시** 댓글은 그대로 남도록 했다.
```
        // answer삭제시 comment는 그대로 둠
        List<Comment> comments = commentRepository.findAllByAnswer(answer);
        for (Comment comment : comments) {
            comment.setAnswer(null);
        }
```
![](https://velog.velcdn.com/images/dohyunii/post/05232126-f1fe-4c91-b15e-4811bb3a636c/image.png)
: answer 삭제 후 위와 달리 comment_id 5의 answer_id가 null로 바뀌었다.

🤔이렇게 하면 나중에 어디에 달렸던 댓글인지 알 수 없지 않나 ..??

**-> soft delete**로 변경

- Answer 엔티티에 추가

 ``` java
 @Where(clause = "is_deleted = false")
 // @Where을 두어 isdeleted=false인 것만 조회하도록 함
 
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
 ```
 
 - Answer을 실제로 삭제하는 대신 is_deleted를 true로 설정하여 관계는 그대로 둔다.
   - answer삭제시 answer_id 5의 is_deleted 가 1로 변경
   ![](https://velog.velcdn.com/images/dohyunii/post/5cfdcc23-dbcb-4902-828a-104cf0157b83/image.png)
   - comment 테이블을 보면, answer_id 5가 그대로 남아있다.
   ![](https://velog.velcdn.com/images/dohyunii/post/6095ebc9-993b-424b-ac14-cad21ca939b6/image.png)
   - 글 조회시, is_deleted=false인 답변만 조회된다.
![](https://velog.velcdn.com/images/dohyunii/post/5e43c3f1-3e8d-49dc-b28d-49fe9c722ef4/image.png)

---
### WEEK 5. Docker
### 1. Docker 컨테이너란?
- 애플리케이션을 패키징하는 툴
- 웹 애플리케이션을 실행하는 데 필요한 모든 환경을 패키징해 컨테이너 이미지를 만들고, 
이 이미지를 이용해 컨테이너를 생성

### 2. Docker의 구성 요소
 **(1) Docker file**
- Copy files
- install dependencies
- set env
- run script 등

 **(2) Docker Image**
- Application을 실행하는 데 필요한 모든 세팅 포함
- 만들어진 이미지는 **불변**

 **(3) Container**
 - image를 이용해 container 안에서 애플리케이션이 동작
 - 격리된 환경에서 실행하며 각 컨테이너는 고유한 파일 시스템을 가짐

### Docker 동작 방식
![img.png](img.png)
**docker file 만들기 -> build해서 docker image 만들기 -> container 구동하기**

### 3. 간단 실습
![](https://velog.velcdn.com/images/dohyunii/post/7329f520-60c9-43a5-a48c-6be5f55e3ddf/image.png)
- hello-world 도커 이미지를 다운로드 받은 후 run 실행

#### <포트포워딩>
![](https://velog.velcdn.com/images/dohyunii/post/07d85280-f3a7-49b8-9ab2-6d276f2a2d91/image.png)
- -p 8080:80 --> 브라우저에서 http://localhost:8080으로 접근하면, 컨테이너의 80번 포트로 연결됨
![](https://velog.velcdn.com/images/dohyunii/post/f7e13235-e8b6-41d2-a086-6615ab24609a/image.png)

#### 그 외
![](https://velog.velcdn.com/images/dohyunii/post/2836144b-76cc-41a3-8650-833a6aee658c/image.png)
- **docker ps** : 현재 실행 중인 컨테이너 목록 조회
- **docker top <컨테이너 name>** : 특정 컨테이너 안에서 실행 중인 프로세스 목록 조회

### 4. 도커 기반 스프링부트 빌드
~~**에러지옥에 빠졌다...**~~

> UnsatisfiedDependencyException. 
 Message: Error creating bean w
ith name 'jwtAuthFilter' defined in URL 
- JwtAuthFilter가 JwtTokenProvider를 생성자 인자로 받고 있는데, 이 과정에서 의존성이 해결되지 않는다고 한다..
- 의존성 문제라면 로컬에서도 에러가 떠야 하는데 잘 돌아갔다.


- 이것저것 고치다가 발견한..
> Caused by: org.springframework.util.PlaceholderResolutionException: Circular placeholder reference 'jwt.secretKey' in value "`${jwt.secretKey}`" <-- "`${jwt.secretKey}`" <-- "`${jwt.secretKey}`"

원래 구현한 application.yml이다.
```java
jwt:
secretKey: `${jwt.secretKey}`
accessTokenExpirationMinutes: 30
refreshTokenExpirationDays: 30
```

여기서 jwt.secretKey 순환참조 오류가 떴다.

>jwt.secretKey: `${jwt.secretKey}`를
>jwt:secretKey: `${JWT_SECRET_KEY}`로 바꿔서 해결

jwt.secretKey를 설정할 때 다시 jwt.secretKey를 참조해 무한 루프가 발생하는 거였다..

아, 그리고 bootJar 사용 시 **application.yml의 내용을 변경**하면 **jar 파일도 다시 빌드**해야 한다. 
여기서도 한참을 헤맸다..

두 번째, **JDBC CONNECTION** 에러

docker-compose.yml
```java
services:
  db:
    image: mysql:8.0
    ports:
      - "3308:3306"
```
docker 컨테이너를 3308 포트로 연결해 뒀다.

application.yml
```java
spring:
  datasource:
    url: "jdbc:mysql://db:3306/naver?useSSL=false&allowPublicKeyRetrieval=true"
```
- docker 호스트 포트는 3308이지만 내부에서는 MYSQL이 3306 포트에서 실행되기 때문에
**jdbc:mysql://db:3306/naver**를 이용해야 한다. 
- 그리고 localhost:3306이 아니라 **docker의 db:3306**으로 url을 바꿔야 한다.

근데도 계속 이 에러가 났다...
![](https://velog.velcdn.com/images/dohyunii/post/f21775be-55d8-4334-96d0-9fad110b307d/image.png)

내가 설정해둔
docker-compose.yml
```java
  app:
    image: doapp
    container_name: spring-app
    env_file:
      - .env
```
여기서 .env파일을 읽어 환경변수를 읽어오도록 했다.
.env 파일에는
```java
AWS_ACCESS_KEY_ID=~~
AWS_BUCKET=~~
AWS_SECRET_ACCESS_KEY=~~
JWT_SECRET_KEY=~~
DB_PASSWORD=~~
```
application.yml은
```java
spring:
  datasource:
    url: "jdbc:mysql://db:3306/naver?useSSL=false&allowPublicKeyRetrieval=true"
    username: root
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
```
이렇게 되어있어 이들을 읽어올 거라 생각했는데 읽어오지 못한 듯 하다.

.env 파일에
```java
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/naver
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=~~
```
추가했더니 드디어 해결됐다.

💥 일단 해결은 됐는데 url과 username은 모두 application.yml에 하드코딩 해두었는데 왜 .env 파일에 추가로 설정해둬야 연결이 되는지 모르겠다.. 
  
application.yml을 읽어오지 못하는 것 같은데 누가 이유를 안다면 알려주세요,,, ㅠ

---
추가로, 에러 해결해보면서 시도해본 
```java
services:
 db:
    healthcheck:
      test: [ "CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      retries: 5

 app:
  depends_on:
   db:
     condition: service_healthy
```
이 방법으로 해결되진 않았지만, **app 서비스가 db가 정상 작동(healthy)일 때만 시작**되도록 제어하기 위한 것이다.

DB가 정상작동되기 전에 App이 실행되면 **connection error**가 뜰 수 있다고 하여 시도해보았다.

---
### WEEK 6. Deploy
### 1. Mission 1 < 도커 배포>
#### (1) EC2
- EC2란 AWS에서 제공하는 **클라우드 컴퓨팅 서비스**로 사용자에게 독립된 컴퓨털르 임대해주는 서비스
  ![](https://velog.velcdn.com/images/dohyunii/post/ef3cf89e-53c4-4cdc-98fa-d1358cbe9f12/image.png)
- ceosBE라는 인스턴스를 만든 후, 탄력적 IP 주소로 **13.209.190.91**를 할당 받았다.
  ![](https://velog.velcdn.com/images/dohyunii/post/a47cc0e1-2884-49ae-9d5f-7009b336f9c0/image.png)
- 보안 규칙으로 HTTPS(443), 웹서버(8080), SSH(22)으로 접속을 허용하며 모든 IP에서 접근 가능하다.

#### (2) 수동 배포
- 도커 이미지 생성 -> 도커 허브에 이미지 올리기
  ![](https://velog.velcdn.com/images/dohyunii/post/9f6cf366-7d3d-48a9-9592-82f68fec8577/image.png)
- 도커 허브에서 이미지 pull 

**sudo docker pull limdodod/dapp** 을 실행한 뒤, spring application에서 만든 docker-compose.yml과 .env를 가져오면 된다.
 
이때, docker-compose.yml에 어떤 이미지를 실행할지 적어줘야 한다.

- 실행 **docker compose up -d**

![](https://velog.velcdn.com/images/dohyunii/post/7c1a177f-9c1f-4f50-adf6-a8fe9ba8fdb2/image.png)
 컨테이너 생성 완료

- 이후 할당 받은 IP주소에서 swagger이 접속됨을 볼 수 있다.

![](https://velog.velcdn.com/images/dohyunii/post/f741327f-6816-4137-bac2-5c389cc7f2bb/image.png)

![](https://velog.velcdn.com/images/dohyunii/post/34ad0768-0d32-420f-9864-e197f2d7c6b6/image.png)
swagger에서 회원가입을 하고 mysql에 접속해 user table에 user가 생긴 것을 볼 수 있다.

#### (3) 탄력적 IP
① 탄력적 IP란?

**동적**인  클라우드 컴퓨팅 시스템에서 **고정된 정적인 IPv4 주소**를 가지는 주소


② 왜 쓰는가?

인스턴스의 Public IP는 고정된 ip주소가 아니라 유동적인 ip 주소이기 때문에 EC2 인스턴스를 재실행하게 되면  기존 IP주소가 변경된다.

Elastic IP는 이를 방지하기 위해 사용한다. 

### 2. Mission 2 <배포환경 Diagram>
<img src="https://velog.velcdn.com/images/dohyunii/post/dd88ff05-abea-4868-a4e9-ff43f5646b4a/image.png" style="width: 60%;">
