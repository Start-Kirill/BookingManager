package org.example.dao;

import org.example.core.entity.Supply;
import org.example.dao.api.ISupplyDao;
import org.example.dao.factory.ds.DataBaseConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SupplyDao implements ISupplyDao {

    private static final String UUID_COLUMN_NAME = "uuid";

    private static final String NAME_COLUMN_NAME = "name";

    private static final String PRICE_COLUMN_NAME = "price";

    private static final String DURATION_COLUMN_NAME = "duration";

    private static final String DT_CREATE_COLUMN_NAME = "dt_create";

    private static final String DT_UPDATE_COLUMN_NAME = "dt_update";

    @Override
    public Optional<Supply> get(UUID uuid) {
        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetOneByUuidSqlStatement())) {
            Supply supply = null;

            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                supply = createSupply(rs);
            }
            return Optional.ofNullable(supply);
        } catch (SQLException e) {
//            TODO
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Supply> get() {

        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetAllSqlStatement())) {
            List<Supply> supplies = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                supplies.add(createSupply(rs));
            }
            return supplies;
        } catch (SQLException e) {
//            TODO
            throw new RuntimeException(e);
        }
    }

    @Override
    public Supply save(Supply supply) {

        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps1 = c.prepareStatement("INSERT INTO app.supply(" +
                     "uuid, name, price, duration, dt_create, dt_update)" +
                     "VALUES (?, ?, ?, ?, ?, ?)")) {
            c.setAutoCommit(false);

            ps1.setObject(1, supply.getUuid());
            ps1.setString(2, supply.getName());
            ps1.setBigDecimal(3, supply.getPrice());
            ps1.setInt(4, supply.getDuration());
            ps1.setObject(5, supply.getDtCreate());
            ps1.setObject(6, supply.getDtUpdate());

            ps1.execute();

            c.commit();

            return supply;
        } catch (SQLException e) {
//            TODO
            throw new RuntimeException(e);
        }


    }

    @Override
    public Supply update(Supply supply) {
        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createUpdateSqlStatement())) {
            Supply updatedSupply = null;

            c.setAutoCommit(false);

            ps.setString(1, supply.getName());
            ps.setBigDecimal(2, supply.getPrice());
            ps.setInt(3, supply.getDuration());
            ps.setObject(4, supply.getUuid());
            ps.setObject(5, supply.getDtUpdate());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                updatedSupply = createSupply(rs);
            }

            c.commit();

            return updatedSupply;
        } catch (SQLException e) {
//            TODO
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Supply supply) {

    }

    private String createGetAllSqlStatement() {
        StringBuilder sb = new StringBuilder("SELECT ");

        sb.append(UUID_COLUMN_NAME);
        sb.append(", ");
        sb.append(NAME_COLUMN_NAME);
        sb.append(", ");
        sb.append(PRICE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DURATION_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" FROM app.supply");

        return sb.toString();
    }

    private String createGetOneByUuidSqlStatement() {
        StringBuilder sb = new StringBuilder(createGetAllSqlStatement());
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ?");
        return sb.toString();
    }

    private String createUpdateSqlStatement() {
        StringBuilder sb = new StringBuilder("UPDATE app.supply SET ");
        sb.append(NAME_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(PRICE_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(DURATION_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = NOW()");
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ? AND ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = ? RETURNING *");
        return sb.toString();
    }

    private Supply createSupply(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);
        String name = rs.getString(NAME_COLUMN_NAME);
        BigDecimal price = rs.getBigDecimal(PRICE_COLUMN_NAME);
        int duration = rs.getInt(DURATION_COLUMN_NAME);
        LocalDateTime dtCreate = rs.getTimestamp(DT_CREATE_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtUpdate = rs.getTimestamp(DT_UPDATE_COLUMN_NAME).toLocalDateTime();
        return new Supply(uuid, name, price, duration, dtCreate, dtUpdate);
    }
}
