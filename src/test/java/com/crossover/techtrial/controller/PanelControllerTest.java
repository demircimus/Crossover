package com.crossover.techtrial.controller;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.model.Panel;
import com.crossover.techtrial.repository.PanelRepository;
import com.crossover.techtrial.service.PanelService;
import com.crossover.techtrial.service.PanelServiceImpl;

/**
 * PanelControllerTest class will test all APIs in PanelController.java.
 * 
 * @author Crossover
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PanelControllerTest {

	MockMvc mockMvc;

	@Mock
	private PanelController panelController;

	@Mock
	private PanelRepository panelRepository;

	@InjectMocks
	private PanelService panelService = new PanelServiceImpl();

	@Autowired
	private TestRestTemplate template;

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(panelController).build();
	}

	@Test
	public void testPanelShouldBeRegistered() throws Exception {
		HttpEntity<Object> panel = getHttpEntity("{\"serial\": \"232323\", \"longitude\": \"54.123232\","
				+ " \"latitude\": \"54.123232\",\"brand\":\"tesla\" }");
		ResponseEntity<Panel> response = template.postForEntity("/api/register", panel, Panel.class);
		Assert.assertEquals(202, response.getStatusCode().value());
	}

	@Test
	public void testDailyElectricityFound() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<ArrayList> response = template.getForEntity("/api/panels/{panel-serial}/daily", ArrayList.class,
				"1234567890123456");
		Assert.assertEquals(200, response.getStatusCode().value());
	}

	@Test
	public void testDailyElectricityNotFound() throws Exception {
		ResponseEntity<Panel> response = template.getForEntity("/api/panels/{panel-serial}/daily", Panel.class,
				"123456227890123456");
		Assert.assertEquals(404, response.getStatusCode().value());
	}

	@Test
	public void testHourlyElectricityFound() {
		ResponseEntity<HourlyElectricity> response = template.getForEntity("/api/panels/{panel-serial}/hourly",
				HourlyElectricity.class, "1234567890123456");
		Assert.assertEquals(200, response.getStatusCode().value());
	}

	@Test
	public void testNotFound() {
		ResponseEntity<HourlyElectricity> response = template.getForEntity("/api/pane/{panel-serial}/hourly",
				HourlyElectricity.class, "1234567890123456");
		Assert.assertEquals(404, response.getStatusCode().value());
	}

	@Test
	public void testHourlyElectricityNotFound() {
		ResponseEntity<HourlyElectricity> response = template.getForEntity("/api/panels/{panel-serial}/hourly",
				HourlyElectricity.class, "12234567890123456");
		Assert.assertEquals(404, response.getStatusCode().value());
	}

	@Test
	public void testSaveHourlyElectricity() {
		HttpEntity<Object> hourlyElectricity = getHttpEntity("{\"panel_id\": \"1\", \"generated_electricity\": \"200\","
				+ " \"reading_at\": \"2018-08-11 09:00:00\"}");
		ResponseEntity<HourlyElectricity> response = template.postForEntity("/api/panels/{panel-serial}/hourly",
				hourlyElectricity, HourlyElectricity.class, "1234567890123456");
		Assert.assertEquals(200, response.getStatusCode().value());
	}

	@Test
	public void testPanelEquals() {
		Panel p1 = new Panel();
		p1.setBrand("tesla");
		Panel p2 = new Panel();
		p2.setBrand("tesla");
		Assert.assertTrue(p1.equals(p2));
	}

	private HttpEntity<Object> getHttpEntity(Object body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<Object>(body, headers);
	}
}
