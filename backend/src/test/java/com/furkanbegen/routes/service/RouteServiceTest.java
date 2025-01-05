package com.furkanbegen.routes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.furkanbegen.routes.dto.LocationDTO;
import com.furkanbegen.routes.dto.RouteDTO;
import com.furkanbegen.routes.dto.TransportationDTO;
import com.furkanbegen.routes.mapper.RouteMapper;
import com.furkanbegen.routes.model.Location;
import com.furkanbegen.routes.model.Transportation;
import com.furkanbegen.routes.model.TransportationType;
import com.furkanbegen.routes.repository.LocationRepository;
import com.furkanbegen.routes.repository.TransportationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

  public static final String TAKSIM_SQUARE = "Taksim Square";
  public static final String ISTANBUL_AIRPORT = "Istanbul Airport";
  public static final String HEATROW_AIRPORT = "Heatrow Airport";
  public static final String WEMBLEY_STADIUM = "Wembley Stadium";
  public static final String BUS = "Bus";
  public static final String FLIGHT = "Flight";
  public static final String UBER = "Uber";
  @Mock private TransportationRepository transportationRepository;

  @Mock private LocationRepository locationRepository;

  @Mock private RouteMapper routeMapper;

  @InjectMocks private RouteService routeService;

  private Location taksimSquare;
  private Location istanbulAirport;
  private Location heatrowAirport;
  private Location wembleyStadium;
  private Transportation taksimSquareToIstanbulAirport;
  private Transportation istanbulAirportToHeatrowAirport;
  private Transportation heatrowAirportToWembleyStadium;

  @BeforeEach
  void setUp() {
    taksimSquare = new Location();
    taksimSquare.setId(1L);
    taksimSquare.setName(TAKSIM_SQUARE);
    taksimSquare.setLatitude(41.0389);
    taksimSquare.setLongitude(28.9862);

    istanbulAirport = new Location();
    istanbulAirport.setId(2L);
    istanbulAirport.setName(ISTANBUL_AIRPORT);
    istanbulAirport.setLatitude(41.2756);
    istanbulAirport.setLongitude(28.7519);

    heatrowAirport = new Location();
    heatrowAirport.setId(3L);
    heatrowAirport.setName(HEATROW_AIRPORT);
    heatrowAirport.setLatitude(51.4700);
    heatrowAirport.setLongitude(0.4543);

    wembleyStadium = new Location();
    wembleyStadium.setId(4L);
    wembleyStadium.setName(WEMBLEY_STADIUM);
    wembleyStadium.setLatitude(51.5560);
    wembleyStadium.setLongitude(0.2795);

    taksimSquareToIstanbulAirport = new Transportation();
    taksimSquareToIstanbulAirport.setId(1L);
    taksimSquareToIstanbulAirport.setFromLocation(taksimSquare);
    taksimSquareToIstanbulAirport.setToLocation(istanbulAirport);
    taksimSquareToIstanbulAirport.setType(TransportationType.OTHER);
    taksimSquareToIstanbulAirport.setName(BUS);
    taksimSquareToIstanbulAirport.setPrice(10.0);
    taksimSquareToIstanbulAirport.setDurationInMinutes(30.0);

    istanbulAirportToHeatrowAirport = new Transportation();
    istanbulAirportToHeatrowAirport.setId(2L);
    istanbulAirportToHeatrowAirport.setFromLocation(istanbulAirport);
    istanbulAirportToHeatrowAirport.setToLocation(heatrowAirport);
    istanbulAirportToHeatrowAirport.setType(TransportationType.FLIGHT);
    istanbulAirportToHeatrowAirport.setName(FLIGHT);
    istanbulAirportToHeatrowAirport.setPrice(200.0);
    istanbulAirportToHeatrowAirport.setDurationInMinutes(180.0);

    heatrowAirportToWembleyStadium = new Transportation();
    heatrowAirportToWembleyStadium.setId(3L);
    heatrowAirportToWembleyStadium.setFromLocation(heatrowAirport);
    heatrowAirportToWembleyStadium.setToLocation(wembleyStadium);
    heatrowAirportToWembleyStadium.setType(TransportationType.OTHER);
    heatrowAirportToWembleyStadium.setName(UBER);
    heatrowAirportToWembleyStadium.setPrice(5.0);
    heatrowAirportToWembleyStadium.setDurationInMinutes(20.0);
  }

  @Test
  void findRoutes_WhenValidLocations_ShouldReturnRoutes() {
    // given
    when(locationRepository.findById(2L)).thenReturn(Optional.of(taksimSquare));
    when(locationRepository.findById(4L)).thenReturn(Optional.of(wembleyStadium));
    when(transportationRepository.findAll())
        .thenReturn(
            List.of(
                taksimSquareToIstanbulAirport,
                istanbulAirportToHeatrowAirport,
                heatrowAirportToWembleyStadium));

    RouteDTO mockRouteDTO = new RouteDTO();
    List<TransportationDTO> transportationDTOs =
        List.of(
            createTransportationDTO(taksimSquareToIstanbulAirport),
            createTransportationDTO(istanbulAirportToHeatrowAirport),
            createTransportationDTO(heatrowAirportToWembleyStadium));
    mockRouteDTO.setTransportations(transportationDTOs);
    mockRouteDTO.setTotalPrice(215.0);
    mockRouteDTO.setTotalDuration(230.0);

    lenient().when(routeMapper.convertToRouteDTO(any())).thenReturn(mockRouteDTO);

    // when
    Page<RouteDTO> routes = routeService.findRoutes(2L, 4L, PageRequest.of(0, 10));

    // then
    assertNotNull(routes);
    assertFalse(routes.isEmpty());
    assertEquals(1, routes.getTotalElements());

    RouteDTO route = routes.getContent().get(0);
    assertNotNull(route);
    assertEquals(3, route.getTransportations().size());
    assertEquals(TAKSIM_SQUARE, route.getTransportations().get(0).getFromLocation().getName());
    assertEquals(ISTANBUL_AIRPORT, route.getTransportations().get(0).getToLocation().getName());
    assertEquals(ISTANBUL_AIRPORT, route.getTransportations().get(1).getFromLocation().getName());
    assertEquals(HEATROW_AIRPORT, route.getTransportations().get(1).getToLocation().getName());
    assertEquals(HEATROW_AIRPORT, route.getTransportations().get(2).getFromLocation().getName());
    assertEquals(WEMBLEY_STADIUM, route.getTransportations().get(2).getToLocation().getName());
    assertEquals(215.0, route.getTotalPrice());
    assertEquals(230.0, route.getTotalDuration());
  }

  @Test
  void findRoutes_WhenValidLocations_ShouldReturnMultipleRoutes() {
    var train = "Train";
    var trainToWembley = new Transportation();
    trainToWembley.setId(6L);
    trainToWembley.setFromLocation(heatrowAirport);
    trainToWembley.setToLocation(wembleyStadium);
    trainToWembley.setType(TransportationType.OTHER);
    trainToWembley.setName(train);
    trainToWembley.setDurationInMinutes(30.0);
    trainToWembley.setPrice(10.0);

    // given
    when(locationRepository.findById(2L)).thenReturn(Optional.of(taksimSquare));
    when(locationRepository.findById(4L)).thenReturn(Optional.of(wembleyStadium));
    when(transportationRepository.findAll())
        .thenReturn(
            List.of(
                taksimSquareToIstanbulAirport,
                istanbulAirportToHeatrowAirport,
                heatrowAirportToWembleyStadium,
                trainToWembley));

    RouteDTO uberRoute = new RouteDTO();
    List<TransportationDTO> uberRouteTransports =
        List.of(
            createTransportationDTO(taksimSquareToIstanbulAirport),
            createTransportationDTO(istanbulAirportToHeatrowAirport),
            createTransportationDTO(heatrowAirportToWembleyStadium));
    uberRoute.setTransportations(uberRouteTransports);
    uberRoute.setTotalPrice(215.0);
    uberRoute.setTotalDuration(230.0);

    RouteDTO trainRoute = new RouteDTO();
    List<TransportationDTO> trainRouteTransports =
        List.of(
            createTransportationDTO(taksimSquareToIstanbulAirport),
            createTransportationDTO(istanbulAirportToHeatrowAirport),
            createTransportationDTO(trainToWembley));
    trainRoute.setTransportations(trainRouteTransports);
    trainRoute.setTotalPrice(220.0);
    trainRoute.setTotalDuration(240.0);

    when(routeMapper.convertToRouteDTO(any()))
        .thenAnswer(
            invocation -> {
              List<Transportation> route = invocation.getArgument(0);
              if (route != null && route.size() == 3) {
                String lastTransportName = route.get(2).getName();
                if (UBER.equals(lastTransportName)) {
                  return uberRoute;
                } else if (train.equals(lastTransportName)) {
                  return trainRoute;
                }
              }
              return null;
            });

    // when
    Page<RouteDTO> routes = routeService.findRoutes(2L, 4L, PageRequest.of(0, 10));

    // then
    assertNotNull(routes);
    assertFalse(routes.isEmpty());
    assertEquals(2, routes.getTotalElements());

    // First route should be Bus -> Flight -> Uber
    RouteDTO firstRoute = routes.getContent().get(0);
    assertEquals(3, firstRoute.getTransportations().size());
    assertEquals(BUS, firstRoute.getTransportations().get(0).getName());
    assertEquals(TAKSIM_SQUARE, firstRoute.getTransportations().get(0).getFromLocation().getName());
    assertEquals(
        ISTANBUL_AIRPORT, firstRoute.getTransportations().get(0).getToLocation().getName());
    assertEquals(FLIGHT, firstRoute.getTransportations().get(1).getName());
    assertEquals(
        ISTANBUL_AIRPORT, firstRoute.getTransportations().get(1).getFromLocation().getName());
    assertEquals(HEATROW_AIRPORT, firstRoute.getTransportations().get(1).getToLocation().getName());
    assertEquals(UBER, firstRoute.getTransportations().get(2).getName());
    assertEquals(
        HEATROW_AIRPORT, firstRoute.getTransportations().get(2).getFromLocation().getName());
    assertEquals(WEMBLEY_STADIUM, firstRoute.getTransportations().get(2).getToLocation().getName());

    // Second route should be Bus -> Flight -> Train
    RouteDTO secondRoute = routes.getContent().get(1);
    assertEquals(3, secondRoute.getTransportations().size());
    assertEquals(BUS, secondRoute.getTransportations().get(0).getName());
    assertEquals(
        TAKSIM_SQUARE, secondRoute.getTransportations().get(0).getFromLocation().getName());
    assertEquals(
        ISTANBUL_AIRPORT, secondRoute.getTransportations().get(0).getToLocation().getName());
    assertEquals(FLIGHT, secondRoute.getTransportations().get(1).getName());
    assertEquals(
        ISTANBUL_AIRPORT, secondRoute.getTransportations().get(1).getFromLocation().getName());
    assertEquals(
        HEATROW_AIRPORT, secondRoute.getTransportations().get(1).getToLocation().getName());
    assertEquals(train, secondRoute.getTransportations().get(2).getName());
    assertEquals(
        HEATROW_AIRPORT, secondRoute.getTransportations().get(2).getFromLocation().getName());
    assertEquals(
        WEMBLEY_STADIUM, secondRoute.getTransportations().get(2).getToLocation().getName());
  }

  @Test
  void findRoutes_WhenFlightFollowedByBus_ShouldReturnRoute() {
    var cityA = new Location();
    cityA.setId(5L);
    cityA.setName("City A");
    cityA.setLatitude(40.0);
    cityA.setLongitude(28.0);

    var cityB = new Location();
    cityB.setId(6L);
    cityB.setName("City B");
    cityB.setLatitude(41.0);
    cityB.setLongitude(29.0);

    var cityC = new Location();
    cityC.setId(7L);
    cityC.setName("City C");
    cityC.setLatitude(42.0);
    cityC.setLongitude(30.0);

    var flightAToB = new Transportation();
    flightAToB.setId(4L);
    flightAToB.setFromLocation(cityA);
    flightAToB.setToLocation(cityB);
    flightAToB.setType(TransportationType.FLIGHT);
    flightAToB.setName("Flight A-B");
    flightAToB.setPrice(150.0);
    flightAToB.setDurationInMinutes(60.0);

    var busToC = new Transportation();
    busToC.setId(5L);
    busToC.setFromLocation(cityB);
    busToC.setToLocation(cityC);
    busToC.setType(TransportationType.OTHER);
    busToC.setName("Bus B-C");
    busToC.setPrice(30.0);
    busToC.setDurationInMinutes(120.0);

    // given
    when(locationRepository.findById(5L)).thenReturn(Optional.of(cityA));
    when(locationRepository.findById(7L)).thenReturn(Optional.of(cityC));
    when(transportationRepository.findAll()).thenReturn(List.of(flightAToB, busToC));

    RouteDTO mockRouteDTO = new RouteDTO();
    mockRouteDTO.setTransportations(List.of());
    lenient().when(routeMapper.convertToRouteDTO(any())).thenReturn(mockRouteDTO);

    // when
    Page<RouteDTO> routes = routeService.findRoutes(5L, 7L, PageRequest.of(0, 10));

    // then
    assertEquals(1, routes.getTotalElements());
  }

  @Test
  void findRoutes_WhenUberFollowedByFlight_ShouldReturnRoute() {
    var cityA = new Location();
    cityA.setId(5L);
    cityA.setName("City A");
    cityA.setLatitude(40.0);
    cityA.setLongitude(28.0);

    var cityB = new Location();
    cityB.setId(6L);
    cityB.setName("City B");
    cityB.setLatitude(41.0);
    cityB.setLongitude(29.0);

    var cityC = new Location();
    cityC.setId(7L);
    cityC.setName("City C");
    cityC.setLatitude(42.0);
    cityC.setLongitude(30.0);

    var uberAToB = new Transportation();
    uberAToB.setId(4L);
    uberAToB.setFromLocation(cityA);
    uberAToB.setToLocation(cityB);
    uberAToB.setType(TransportationType.OTHER);
    uberAToB.setName("Uber A-B");
    uberAToB.setPrice(40.0);
    uberAToB.setDurationInMinutes(45.0);

    var flightToC = new Transportation();
    flightToC.setId(5L);
    flightToC.setFromLocation(cityB);
    flightToC.setToLocation(cityC);
    flightToC.setType(TransportationType.FLIGHT);
    flightToC.setName("Flight B-C");
    flightToC.setPrice(200.0);
    flightToC.setDurationInMinutes(90.0);

    // given
    when(locationRepository.findById(5L)).thenReturn(Optional.of(cityA));
    when(locationRepository.findById(7L)).thenReturn(Optional.of(cityC));
    when(transportationRepository.findAll()).thenReturn(List.of(uberAToB, flightToC));

    RouteDTO mockRouteDTO = new RouteDTO();
    mockRouteDTO.setTransportations(List.of());
    lenient().when(routeMapper.convertToRouteDTO(any())).thenReturn(mockRouteDTO);

    // when
    Page<RouteDTO> routes = routeService.findRoutes(5L, 7L, PageRequest.of(0, 10));

    // then
    assertEquals(1, routes.getTotalElements());
  }

  @Test
  void findRoutes_WhenSingleFlight_ShouldReturnRoute() {
    // Create locations
    var cityA = new Location();
    cityA.setId(5L);
    cityA.setName("City A");
    cityA.setLatitude(40.0);
    cityA.setLongitude(28.0);

    var cityB = new Location();
    cityB.setId(6L);
    cityB.setName("City B");
    cityB.setLatitude(41.0);
    cityB.setLongitude(29.0);

    // Create transportation
    var flightAToB = new Transportation();
    flightAToB.setId(4L);
    flightAToB.setFromLocation(cityA);
    flightAToB.setToLocation(cityB);
    flightAToB.setType(TransportationType.FLIGHT);
    flightAToB.setName("Flight A-B");
    flightAToB.setPrice(150.0);
    flightAToB.setDurationInMinutes(60.0);

    // given
    when(locationRepository.findById(5L)).thenReturn(Optional.of(cityA));
    when(locationRepository.findById(6L)).thenReturn(Optional.of(cityB));
    when(transportationRepository.findAll()).thenReturn(List.of(flightAToB));

    RouteDTO mockRouteDTO = new RouteDTO();
    List<TransportationDTO> transportationDTOs = List.of(createTransportationDTO(flightAToB));
    mockRouteDTO.setTransportations(transportationDTOs);
    mockRouteDTO.setTotalPrice(150.0);
    mockRouteDTO.setTotalDuration(60.0);

    lenient().when(routeMapper.convertToRouteDTO(any())).thenReturn(mockRouteDTO);

    // when
    Page<RouteDTO> routes = routeService.findRoutes(5L, 6L, PageRequest.of(0, 10));

    // then
    assertNotNull(routes);
    assertFalse(routes.isEmpty());
    assertEquals(1, routes.getTotalElements());

    RouteDTO route = routes.getContent().get(0);
    assertNotNull(route);
    assertEquals(1, route.getTransportations().size());
    assertEquals(150.0, route.getTotalPrice());
    assertEquals(60.0, route.getTotalDuration());
  }

  @Test
  void findRoutes_WhenMoreThanOneTransportationBeforeFlight_ShouldReturnEmptyList() {
    var home = new Location();
    home.setId(11L);
    home.setName("Home");
    home.setLatitude(41.0082);
    home.setLongitude(28.9784);

    var homeToTaksim = new Transportation();
    homeToTaksim.setId(11L);
    homeToTaksim.setFromLocation(home);
    homeToTaksim.setToLocation(taksimSquare);
    homeToTaksim.setType(TransportationType.OTHER);
    homeToTaksim.setName(UBER);

    // given
    when(locationRepository.findById(11L)).thenReturn(Optional.of(home));
    when(locationRepository.findById(4L)).thenReturn(Optional.of(wembleyStadium));
    when(transportationRepository.findAll())
        .thenReturn(
            List.of(
                homeToTaksim,
                taksimSquareToIstanbulAirport,
                istanbulAirportToHeatrowAirport,
                heatrowAirportToWembleyStadium));

    RouteDTO mockRouteDTO = new RouteDTO();
    mockRouteDTO.setTransportations(List.of());
    lenient().when(routeMapper.convertToRouteDTO(any())).thenReturn(mockRouteDTO);

    // when
    Page<RouteDTO> routes = routeService.findRoutes(11L, 4L, PageRequest.of(0, 10));

    // then
    assertEquals(0, routes.getTotalElements());
  }

  @Test
  void findRoutes_WhenTwoFlightsInSequence_ShouldReturnEmptyList() {
    var paris = new Location();
    paris.setId(11L);
    paris.setName("paris");
    paris.setLatitude(41.0082);
    paris.setLongitude(28.9784);

    var istanbulToParis = new Transportation();
    istanbulToParis.setId(11L);
    istanbulToParis.setFromLocation(istanbulAirport);
    istanbulToParis.setToLocation(taksimSquare);
    istanbulToParis.setType(TransportationType.OTHER);
    istanbulToParis.setName(UBER);

    // given
    when(locationRepository.findById(2L)).thenReturn(Optional.of(istanbulAirport));
    when(locationRepository.findById(11L)).thenReturn(Optional.of(paris));
    when(transportationRepository.findAll())
        .thenReturn(
            List.of(
                taksimSquareToIstanbulAirport, istanbulToParis, istanbulAirportToHeatrowAirport));

    RouteDTO mockRouteDTO = new RouteDTO();
    mockRouteDTO.setTransportations(List.of());
    lenient().when(routeMapper.convertToRouteDTO(any())).thenReturn(mockRouteDTO);

    // when
    Page<RouteDTO> routes = routeService.findRoutes(2L, 11L, PageRequest.of(0, 10));

    // then
    assertEquals(0, routes.getTotalElements());
  }

  private TransportationDTO createTransportationDTO(Transportation transportation) {
    var transportationDTO = new TransportationDTO();
    transportationDTO.setId(transportation.getId());
    transportationDTO.setToLocation(createLocationDTO(transportation.getToLocation()));
    transportationDTO.setFromLocation(createLocationDTO(transportation.getFromLocation()));
    transportationDTO.setName(transportation.getName());
    transportationDTO.setType(transportation.getType());
    transportationDTO.setPrice(transportation.getPrice());
    transportationDTO.setDurationInMinutes(transportation.getDurationInMinutes());
    return transportationDTO;
  }

  private LocationDTO createLocationDTO(final Location toLocation) {
    var locationDTO = new LocationDTO();
    locationDTO.setId(toLocation.getId());
    locationDTO.setName(toLocation.getName());
    locationDTO.setLatitude(toLocation.getLatitude());
    locationDTO.setLongitude(toLocation.getLongitude());
    return locationDTO;
  }
}
