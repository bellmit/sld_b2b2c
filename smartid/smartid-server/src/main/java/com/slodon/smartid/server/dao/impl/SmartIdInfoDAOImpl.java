package com.slodon.smartid.server.dao.impl;

import com.slodon.smartid.server.dao.SmartIdInfoDAO;
import com.slodon.smartid.server.dao.entity.SmartIdInfo;
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
public class SmartIdInfoDAOImpl implements SmartIdInfoDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public SmartIdInfo queryByBizType(String bizType) {
        String sql = "select id, biz_type, begin_id, max_id," +
                " step, delta, remainder, create_time, update_time, version" +
                " from smart_id_info where biz_type = ?";
        List<SmartIdInfo> list = jdbcTemplate.query(sql, new Object[]{bizType}, new SmartIdInfoRowMapper());
        if(list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public int updateMaxId(Long id, Long newMaxId, Long oldMaxId, Long version, String bizType) {
        String sql = "update smart_id_info set max_id= ?," +
                " update_time=now(), version=version+1" +
                " where id=? and max_id=? and version=? and biz_type=?";
        return jdbcTemplate.update(sql, newMaxId, id, oldMaxId, version, bizType);
    }


    public static class SmartIdInfoRowMapper implements RowMapper<SmartIdInfo> {

        @Override
        public SmartIdInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            SmartIdInfo smartIdInfo = new SmartIdInfo();
            smartIdInfo.setId(resultSet.getLong("id"));
            smartIdInfo.setBizType(resultSet.getString("biz_type"));
            smartIdInfo.setBeginId(resultSet.getLong("begin_id"));
            smartIdInfo.setMaxId(resultSet.getLong("max_id"));
            smartIdInfo.setStep(resultSet.getInt("step"));
            smartIdInfo.setDelta(resultSet.getInt("delta"));
            smartIdInfo.setRemainder(resultSet.getInt("remainder"));
            smartIdInfo.setCreateTime(resultSet.getDate("create_time"));
            smartIdInfo.setUpdateTime(resultSet.getDate("update_time"));
            smartIdInfo.setVersion(resultSet.getLong("version"));
            return smartIdInfo;
        }
    }
}
