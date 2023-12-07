package csw.lms.namsan.nodes.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// NAVER Oauth2: {resultcode=00, message=success, response={id=OZZK_hI1Cgrp-auSBfFh1234, nickname=nick, profile_image=https://ssl.pstatic.net/static/pwe/address/img_profile.png, email=EMAIL, name=NAME}}
// KAKAO Oauth2: {id=1234, connected_at=2023-08-05T14:33:53Z, kakao_account={profile_nickname_needs_agreement=false, profile_image_needs_agreement=false, profile={nickname=석원 Seok Won, thumbnail_image_url=http://k.kakaocdn.net/dn/bGwedH/btr7QnzItcR/Ah54rAOX8RSdts93mvFzgk/img_110x110.jpg, profile_image_url=http://k.kakaocdn.net/dn/bGwedH/btr7QnzItcR/Ah54rAOX8RSdts93mvFzgk/img_640x640.jpg, is_default_image=false}, has_email=true, email_needs_agreement=false, is_email_valid=true, is_email_verified=true, email=ikr@kakao.com, has_age_range=true, age_range_needs_agreement=true}, properties={nickname=석원 Seok Won, profile_image=http://k.kakaocdn.net/dn/bGwedH/btr7QnzItcR/Ah54rAOX8RSdts93mvFzgk/img_640x640.jpg, thumbnail_image=http://k.kakaocdn.net/dn/bGwedH/btr7QnzItcR/Ah54rAOX8RSdts93mvFzgk/img_110x110.jpg}}
// GOOGLE Oauth2: {sub=1234, email_verified=true, name=name, given_name=givenname, locale=ko, hd=mail, family_name=sur, picture=https://lh3.googleusercontent.com/a/pic, email=email}
// GITHUB Oauth2: {gists_url=https://api.github.com/users/user/gists{/gist_id}, repos_url=https://api.github.com/users/user/repos, two_factor_authentication=true, following_url=https://api.github.com/users/user/following{/other_user}, twitter_username=null, bio=bioo, created_at=2010-09-16T11:47:24Z, login=id, type=User, blog=blog, private_gists=16, total_private_repos=6, subscriptions_url=https://api.github.com/users/user/subscriptions, updated_at=2023-11-24T12:19:23Z, site_admin=false, disk_usage=1234, collaborators=3, company=company, owned_private_repos=16, id=1234, public_repos=134, gravatar_id=, plan={name=pro, space=1234, collaborators=0, private_repos=9999}, email=email, organizations_url=https://api.github.com/users/user/orgs, hireable=true, starred_url=https://api.github.com/users/user/starred{/owner}{/repo}, followers_url=https://api.github.com/users/user/followers, public_gists=29, url=https://api.github.com/users/user, received_events_url=https://api.github.com/users/user/received_events, followers=67, avatar_url=https://avatars.githubusercontent.com/u/1234?v=4, events_url=https://api.github.com/users/user/events{/privacy}, html_url=https://github.com/user, following=74, name=name, location=South Korea, node_id=afs=}

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuth2ViewController {

//    private final HttpSession httpSession;

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    // FOR SERVER ONLY TEST
//    @GetMapping("/home")
//    public ModelAndView oauth() {
//        var mav = new ModelAndView("home");
//        var user = (SessionUser) httpSession.getAttribute("user");
//        mav.addObject("picture", user.getPicture());
//        mav.addObject("email", user.getEmail());
//        mav.addObject("name", user.getName());
//
//        return mav;
//    }
//
//    // NOT BEING USED
//    @GetMapping("/login")
//    public ModelAndView login() {
//        ModelAndView mav = new ModelAndView();
//        mav.setViewName("login");  // this refers to login.html in the templates directory
//        return mav;
//    }
}
