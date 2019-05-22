package com.lzhbatis;


import com.lzhbatis.mapper.BlogMapper;

public class MyBatisBoot {
    public static void main(String[] args) {
        LzhSqlSession sqlSession = new LzhSqlSession(new LzhConfiguration(), new LzhExecutor());
        BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
        blogMapper.selectBlogById(1);
    }
}
