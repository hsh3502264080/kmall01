package com.kgc.kmall.service;

import com.kgc.kmall.bean.Member;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-15 16:09
 */
public interface MemberService {
    public List<Member> selectAllMember();

    Member login(Member member);

    void addUserToken(String token, Long id);
}
