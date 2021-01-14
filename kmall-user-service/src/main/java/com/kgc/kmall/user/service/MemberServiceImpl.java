package com.kgc.kmall.user.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.Member;
import com.kgc.kmall.bean.MemberExample;
import com.kgc.kmall.manager.util.RedisUtil;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.user.mapper.MemberMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-15 16:10
 */
@Service
@Component
public class MemberServiceImpl implements MemberService {
    @Resource
    MemberMapper memberMapper;
    @Resource
    RedisUtil redisUtil;

    @Override
    public List<Member> selectAllMember()
    {
        System.out.println(111);
        return memberMapper.selectByExample(null);
    }

    @Override
    public Member login(Member member) {
        System.out.println("走了IMPL的Login");
        //先走redis
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            if (jedis != null) {
                System.out.println("走了redis");
                String usemember = jedis.get("user:" + member.getId() + ":info");
                if (usemember != null) {//密码正确
                    Member umsMemberFromCache = JSON.parseObject(usemember, Member.class);
                    return umsMemberFromCache;
                }
            }
            //连接redis失败，关系型DB
            Member umsMemberFromDb = loginFromDb(member);
            if (umsMemberFromDb != null) {
                System.out.println("走了DB");
                jedis.setex("user:" + umsMemberFromDb.getId() + ":info", 60 * 60 * 24, JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        System.out.println(11);
        return null;
    }

    @Override
    public void addUserToken(String token, Long memberId) {
        Jedis jedis = redisUtil.getJedis();

        jedis.setex("user:" + memberId + ":token", 60 * 60 * 2, token);

        jedis.close();

    }



    private Member loginFromDb(Member member) {
        MemberExample example=new MemberExample();
        MemberExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(member.getUsername());
        criteria.andPasswordEqualTo(member.getPassword());
        List<Member> members = memberMapper.selectByExample(example);
        System.out.println("密码"+member.getPassword());
        System.out.println("用户名"+member.getUsername());
        if (members.size()>0){
            System.out.println(members.get(0));
            return members.get(0);
        }
        return null;
    }



}
