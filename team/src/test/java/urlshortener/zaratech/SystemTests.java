package urlshortener.zaratech;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.GreaterThan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import urlshortener.common.repository.*;
import urlshortener.zaratech.web.UrlShortenerControllerWithLogs;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class SystemTests {

    private static final Logger logger = LoggerFactory.getLogger(SystemTests.class);

    @Autowired
    protected ClickRepository clickRepository;

    @Value("${local.server.port}")
    private int port = 0;

    @Test
    public void testHome() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port,
                String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertTrue(entity.getHeaders().getContentType().isCompatibleWith(new MediaType("text", "html")));
        assertThat(entity.getBody(), containsString("<title>URL"));
    }

    @Test
    public void testCss() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/webjars/bootstrap/3.3.5/css/bootstrap.min.css", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getHeaders().getContentType(), is(MediaType.valueOf("text/css")));
        assertThat(entity.getBody(), containsString("body"));
    }

    @Test
    public void testCreateLink() throws Exception {
        ResponseEntity<String> entity = postLink("http://example.com/");
        assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(entity.getHeaders().getLocation(), is(new URI("http://localhost:" + this.port + "/f684a3c4")));
        assertThat(entity.getHeaders().getContentType(),
                is(new MediaType("application", "json", Charset.forName("UTF-8"))));
        ReadContext rc = JsonPath.parse(entity.getBody());
        assertThat(rc.read("$.hash"), is("f684a3c4"));
        assertThat(rc.read("$.uri"), is("http://localhost:" + this.port + "/f684a3c4"));
        assertThat(rc.read("$.target"), is("http://example.com/"));
        assertThat(rc.read("$.sponsor"), is(nullValue()));
        URI uriQR = new URI(
                "http://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + rc.read("$.uri") + "&choe=UTF-8&chld=L");
        assertThat(rc.read("$.qr"), is(uriQR.toString()));

        // TODO comprobar que una peticion HTTP GET a la uri original no es una
        // redireccion a la misma uri
    }

    @Test
    public void testRedirection() throws Exception {
        postLink("http://example.com/");
        ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port + "/f684a3c4", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
        assertThat(entity.getHeaders().getLocation(), is(new URI("http://example.com/")));
    }

    private ResponseEntity<String> postLink(String url) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("url", url);
        return new TestRestTemplate().postForEntity("http://localhost:" + this.port + "/link-single", parts,
                String.class);
    }

    @Test
    public void testDetail() throws Exception {
        ResponseEntity<String> entity = postLink("http://example.com/");
        assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(entity.getHeaders().getLocation(), is(new URI("http://localhost:" + this.port + "/f684a3c4")));
        assertThat(entity.getHeaders().getContentType(),
                is(new MediaType("application", "json", Charset.forName("UTF-8"))));
        ReadContext rc = JsonPath.parse(entity.getBody());
        String fecha = getDate();

        // Testing date detail
        assertThat(rc.read("$.created").toString(), is(fecha));

        // Testing clicks detail
        long clicks = clickRepository.clicksByHash(rc.read("$.hash").toString());
        entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/f684a3c4", String.class);
        entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/f684a3c4", String.class);
        clicks += 2;
        assertThat(clickRepository.clicksByHash(rc.read("$.hash").toString()).toString(),
                is(String.valueOf(((int) clicks))));
    }

    /*
     * Return a string with the current date
     */
    private String getDate() {
        Calendar date = Calendar.getInstance();

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);

        return String.format("%d-%02d-%02d", year, month, day);
    }

    @Test
    public void testMultiUpload() throws Exception {

        ResponseEntity<String> postResp = postFile();

        // test the URI creation
        assertThat(postResp.getStatusCode(), is(HttpStatus.CREATED));

        // test first URI --> http://example.com/
        ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port + "/f684a3c4", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
        assertThat(entity.getHeaders().getLocation(), is(new URI("http://example.com/")));

        // test last URI --> http://google.com/
        entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/5e399431", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
        assertThat(entity.getHeaders().getLocation(), is(new URI("http://google.com/")));

        // clean test file
        deleteCsvFile();
    }

    /**
     * Post a CSV file to the 'link-multi' endpoint
     */
    private ResponseEntity<String> postFile() {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        parts.add("url", new FileSystemResource(generateCsvFile()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new TestRestTemplate().postForEntity("http://localhost:" + this.port + "/link-multi", parts,
                String.class);
    }

    @Test
    public void testMultiUploadAsync() throws Exception {

        ResponseEntity<String> postResp = postFileAsync();

        // test if the TASK has been accepted
        assertThat(postResp.getStatusCode(), is(HttpStatus.ACCEPTED));

        // test if there is an URI which identifies the TASK
        assertThat(postResp.hasBody(), is(true));

        String taskUrl = postResp.getBody();

        assertNotNull(taskUrl);

        String partsArray[] = taskUrl.split("\":\"");

        assertThat(partsArray.length, is(2));

        taskUrl = partsArray[1];
        taskUrl = taskUrl.replaceAll("[}\\\"]", "");

        assertThat(taskUrl, not(equalTo("")));

        // test TASK URI
        ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity(taskUrl, String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        
        assertThat(entity.hasBody(), is(true));

        String urlList = entity.getBody();

        assertNotNull(urlList);
        assertThat(urlList, not(equalTo("")));
        
        // test URIs List
        String[] urls = {"http://example.com/", "http://example2.com/", "http://github.com/", "http://unizar.es/", "http://google.com/"};
        for(String url : urls){
            assertTrue(urlList.indexOf(url) > 0);
        }
        
        // test URIs progress
        int occurances = StringUtils.countOccurrencesOf(urlList, "progress");
        assertThat(occurances, is(urls.length));

        // clean test file
        deleteCsvFile();
    }

    /**
     * Post a CSV file to the 'link-multi' endpoint
     */
    private ResponseEntity<String> postFileAsync() {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        parts.add("url", new FileSystemResource(generateCsvFile()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new TestRestTemplate().postForEntity("http://localhost:" + this.port + "/link-multi-async", parts,
                String.class);
    }

    /**
     * Generate an example CSV file
     */
    private File generateCsvFile() {

        File csv = new File("test.csv");

        if (csv.exists()) {
            csv.delete();
        }

        PrintWriter pw;
        try {
            pw = new PrintWriter(csv);
            pw.println("http://example.com/, http://example2.com/,");
            pw.println("http://github.com/, http://unizar.es/, http://google.com/");

            pw.flush();
            pw.close();

            return csv;

        } catch (FileNotFoundException e) {

            return null;
        }
    }

    /**
     * Delete the example CSV file
     */
    private void deleteCsvFile() {
        File csv = new File("test.csv");

        if (csv.exists()) {
            csv.delete();
        }
    }
}
