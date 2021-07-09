package com.slodon.smartid.server.dao.impl;

import com.slodon.smartid.server.dao.SmartIdTokenDAO;
import com.slodon.smartid.server.dao.entity.SmartIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author smartId
 */
@Repository
public class SmartIdTokenDAOImpl implements SmartIdTokenDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<SmartIdToken> selectAll() {
        String sql = "select id, token, biz_type, remark, " +
                "create_time, update_time from smart_id_token";
        return jdbcTemplate.query(sql, new SmartIdTokenRowMapper());
    }

    public static class SmartIdTokenRowMapper implements RowMapper<SmartIdToken> {

        @Override
        public SmartIdToken mapRow(ResultSet resultSet, int i) throws SQLException {
            SmartIdToken token = new SmartIdToken();
            token.setId(resultSet.getInt("id"));
            token.setToken(resultSet.getString("token"));
            token.setBizType(resultSet.getString("biz_type"));
            token.setRemark(resultSet.getString("remark"));
            token.setCreateTime(resultSet.getDate("create_time"));
            token.setUpdateTime(resultSet.getDate("update_time"));
            return token;
        }
    }
}
