package com.crossover.trial.weather.controller;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AirportServiceImpl;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.crossover.trial.weather.service.AtmosphericInformationServiceImpl;
import com.crossover.trial.weather.utils.ConstantHelper;
import com.crossover.trial.weather.utils.GsonFactory;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

    private AirportService airportService;

    private AtmosphericInformationService atmosphericInformationService;

    public RestWeatherQueryEndpoint() {
        airportService = new AirportServiceImpl();
        atmosphericInformationService = new AtmosphericInformationServiceImpl();
    }

    /** atmospheric information for each airport, idx corresponds with airportData */
    protected List<AtmosphericInformation> atmosphericInformation = Collections.synchronizedList(new LinkedList<>());

    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link #ping()}
     */
    public Map<AirportData, Integer> requestFrequency = new HashMap<AirportData, Integer>();

    public Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @Override
    public String ping() {
        Map<String, Object> retval = new HashMap<>();

        long datasize = atmosphericInformationService.getAllAtmosphericInformation().stream()
            .filter(atmospheicInfo ->
                atmospheicInfo.getLastUpdateTime() > System.currentTimeMillis() - ConstantHelper.ONE_DAY)
            .count();

        retval.put("datasize", datasize);

        Map<String, Double> freq = new HashMap<>();
        // fraction of queries
        for (AirportData data : airportService.getAllAirportData()) {
            double frac = (double)requestFrequency.getOrDefault(data, 0) / requestFrequency.size();
            freq.put(data.getIata(), frac);
        }
        retval.put("iata_freq", freq);

        int m = radiusFreq.keySet().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
            int i = e.getKey().intValue() % 10;
            hist[i] += e.getValue();
        }
        retval.put("radius_freq", hist);

        return GsonFactory.toJson(retval);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @GET
    @Path("/weather/{iata}/{radius}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response weather(@PathParam("iata") String iata, @PathParam("radius") String radiusString) {
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        updateRequestFrequency(iata, radius);
        List<AirportData> airportData = airportService.getAirportDataInRadius(iata, radius);
        return Response.status(Response.Status.OK)
            .entity(atmosphericInformationService.getAtmosphericInformationForAirports(airportData))
            .build();
    }

    /**
     * Records information about how often requests are made
     *
     * @param iata an iata code
     * @param radius query radius
     */
    public void updateRequestFrequency(String iata, Double radius) {
        AirportData airportData = airportService.findAirportData(iata);
        requestFrequency.put(airportData, requestFrequency.getOrDefault(airportData, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0));
    }

}
