formlogin 구현완료
oauth2 로그인 구현완료
폼로그인과 oauth로그인 동시에 작동하게 구현완료

07/11  
TODOs  

**!해결**-회원가입 에러 페이지 정상 작동하는지 확인(정상작동)

**!해결**-로그인 -> 로그아웃 후 다시 로그인 하면 로그인이 안되는 오류 발생.
Security와 Spring이 가지고 있는 세션이 달라서 발생하는 문제.  
(HttpSessionEventPublisher 를 리스너로 빈에 등록해 해결)

-로그인한 상태에서 회원가입, 로그인 폼 창으로 가지 못하게 하는 코드 작성

**!해결**-회원가입 시 ID, Email 체크하는 api만들기  
(bindingresult.rejectvalue에서 defaultmessage가 타임리프에 구현되게 하는데 문제가 있었다.
@ModelAttribute로 'user'라는 이름으로 데이터를 받았는데 user.username으로 입력하지 않고 username으로 입력해서 문제가 발생했었다.)  

**!해결**-로그인 실패시 오류 메시지 출력하고 다시 로그인폼으로 이동하는 기능 구현  
("${param.error}" 로 url에 error가 있을 경우를 판단한다. Spring Security는
기본적으로 로그인 실패시 자동으로 url에 '?error' 파라미터를 추가하기 때문에 이를 이용해서 구현했다.)

블로그 게시판 기능 구현 예정,,

인가 시스템 구현예정,,

css 설정

jwt 로그인구현 예정,,
(후순위,,)



,,,
