package com.kgc.kmall.user.controller;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.service.MemberService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-15 17:08
 */
@RestController
public class MemberController {
    @Reference
    MemberService memberService;
    @RequestMapping("/index")
    public List<Member> test(){
        List<Member> members = memberService.selectAllMember();
        return members;
    }

}
