package berman.andrew;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Main application class
 *
 * @author Andrew Berman
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    //Constants
    private static final String FILENAME = "results.csv";
    private static final Range<Double> US_LATITUDE_RANGE = Range.between(-125.0011, -66.9325);
    private static final Range<Double> US_LONGITUDE_RANGE = Range.between(24.9493, 49.5904);
    private static final String ENDPOINT = "http://localhost:8080/points";

    private enum City {
        TOKYO(35.6833, 139.6833),
        SYDNEY(-33.8650, 151.2094),
        RIYADH(24.6333, 46.7167),
        ZURICH(47.3667, 8.5500),
        REYKJAVIK(64.1333, -21.9333),
        MEXICO_CITY(19.0000, -99.1333),
        LIMA(-12.0433, -77.0283);

        private Point point;

        private City(double latitude, double longitude) {
            this.point = new Point(latitude, longitude);
        }

        public Point getPoint() {
            return this.point;
        }
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);

        RestTemplate restTemplate = new RestTemplate();
        Point[] points = restTemplate.getForObject(ENDPOINT, Point[].class);

        List<String> spreadsheet = new ArrayList<>();
        spreadsheet.add("Latitude, Longitude, Within US, 500 mi from Tokyo, Dist from Tokyo, 500 mi from Sydney, Dist from Sydney, 500 mi from Riyadh, Dist from Riyadh, 500 mi from Zurich, Dist from Zurich, 500 mi from Reykjavik, Dist from Reykjavik, 500 mi from Mexico City, Dist from Mexico City, 500 mi from Lima, Dist from Lima");

        for (Point point : points) {
            List<String> line = new ArrayList<>();
            line.add(point.getLatitude().toString());
            line.add(point.getLongitude().toString());

            boolean isContiguousUS = US_LATITUDE_RANGE.contains(point.getLatitude()) && US_LONGITUDE_RANGE.contains(point
                    .getLongitude());

            //Is point in US?
            line.add(String.valueOf(isContiguousUS));

            if (!isContiguousUS) {
                double[] distances = {
                        point.getDistanceInMiles(City.TOKYO.getPoint()),
                        point.getDistanceInMiles(City.SYDNEY.getPoint()),
                        point.getDistanceInMiles(City.RIYADH.getPoint()),
                        point.getDistanceInMiles(City.ZURICH.getPoint()),
                        point.getDistanceInMiles(City.REYKJAVIK.getPoint()),
                        point.getDistanceInMiles(City.MEXICO_CITY.getPoint()),
                        point.getDistanceInMiles(City.LIMA.getPoint())
                };
                
                for (double dist : distances) {
                    //Is point within 500 miles of city?
                    line.add(String.valueOf(dist <= 500));

                    //Distance from each city
                    line.add(String.valueOf(dist));
                }
            }

            spreadsheet.add(StringUtils.join(line, ","));
        }

        FileUtils.writeLines(new File(FILENAME), spreadsheet);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Start inserting 10,000 random points into the database");
        Random random = new Random();

        final String sql = "insert into points (latitude, longitude) values (?, ?)";

        //Ensures there are 10,000 unique points
        Set<Point> points = new HashSet<>();
        while (points.size() < 10000) {
            double temp = random.nextDouble();
            //-90 to 90
            double latitude = -90.0 + temp * 180.0;

            //-180 to 180
            double longitude = -180.0 + temp * 360.0;

            points.add(new Point(latitude, longitude));
        }

        final Point[] pointArray = points.toArray(new Point[points.size()]);

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Point point = pointArray[i];
                ps.setDouble(1, point.getLatitude());
                ps.setDouble(2, point.getLongitude());
            }

            @Override
            public int getBatchSize() {
                return pointArray.length;
            }
        });

        log.info("Finished inserting 10,000 random points into the database");
    }
}
