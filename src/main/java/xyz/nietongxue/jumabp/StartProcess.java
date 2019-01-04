package xyz.nietongxue.jumabp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.Executors;

@Configuration
public class StartProcess {


    @MessagingGateway
    public interface ProcessStarter {

        @Gateway(requestChannel = "eventStartFlow.input")
        void placeBE(BE be);

    }

    @Bean
    public IntegrationFlow eventStartFlow() {
        return f -> f
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .handle(m ->

                );
    }




    public void testGetEmployeeAsJson() throws Exception{
        Map<String, Object> employeeSearchMap = getEmployeeSearchMap("0");

        final String fullUrl = "http://localhost:8080/rest-http/services/employee/{id}/search?format=json";
        HttpHeaders headers = getHttpHeadersWithUserCredentials(new HttpHeaders());
        headers.add("Accept", "application/json");
        HttpEntity<Object> request = new HttpEntity<Object>(headers);

        ResponseEntity<?> httpResponse = restTemplate.exchange(fullUrl, HttpMethod.GET, request, EmployeeList.class, employeeSearchMap);
        logger.info("Return Status :"+httpResponse.getHeaders().get("X-Return-Status"));
        logger.info("Return Status Message :"+httpResponse.getHeaders().get("X-Return-Status-Msg"));
        assertTrue(httpResponse.getStatusCode().equals(HttpStatus.OK));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jaxbJacksonObjectMapper.writeValue(out, httpResponse.getBody());
        logger.info(new String(out.toByteArray()));
    }

    private HttpHeaders getHttpHeadersWithUserCredentials(ClientHttpRequest request){
        return (getHttpHeadersWithUserCredentials(request.getHeaders()));
    }

    private HttpHeaders getHttpHeadersWithUserCredentials(HttpHeaders headers){

        String username = "SPRING";
        String password = "spring";

        String combinedUsernamePassword = username+":"+password;
        byte[] base64Token = Base64.getEncoder().encode(combinedUsernamePassword.getBytes());
        String base64EncodedToken = new String (base64Token);
        //adding Authorization header for HTTP Basic authentication
        headers.add("Authorization","Basic  "+base64EncodedToken);

        return headers;
    }
}
