package berman.andrew;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * REST Controller which defines the API
 *
 * @author Andrew Berman
 */
@RestController
@RequestMapping("/points")
public class PointController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    /**
     * Retreives all the points in the database
     * @return The list of all points in the system
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Point> all() {
        log.info("Querying for all points");
        return jdbcTemplate.query("select * from points", new RowMapper<Point>() {
            @Override
            public Point mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Point(rs.getDouble(1), rs.getDouble(2));
            }
        });
    }

    /**
     * Queries the system as to whether the point already exists
     * @param latitude
     * @param longitude
     * @return A boolean as to whether the point exists
     */
    @RequestMapping(value = "/{lat:.+}:{long:.+}", method = RequestMethod.GET)
    public Boolean get(@PathVariable(value = "lat") Double latitude, @PathVariable(value = "long") Double longitude) {
        log.info("Querying for point: " + latitude + ", " + longitude);
        final String sql = "select count(*) from points where latitude = ? and longitude = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, latitude, longitude) > 0;
    }

    /**
     * Saves a point to the database if it does not already exist
     * @param point
     * @return
     *  HTTP 201 if point did not exist and was successfully inserted into db
     *  HTTP 302 if point already existed in db
     *  HTTP 400 if there was a problem converting response body to a Point object
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> add(@RequestBody Point point) {
        if (point != null) {
            log.info("Adding new point: " + point.toString());
            final String sql = "insert into points (latitude, longitude) values (?, ?)";
            try {
                jdbcTemplate.update(sql, point.getLatitude(), point.getLongitude());
                return new ResponseEntity<String>(HttpStatus.CREATED);
            } catch (DuplicateKeyException ex) {
                return new ResponseEntity<String>(HttpStatus.FOUND);
            }
        }

        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
}
