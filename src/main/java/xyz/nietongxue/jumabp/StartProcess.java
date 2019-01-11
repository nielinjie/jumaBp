package xyz.nietongxue.jumabp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.Channels;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

@Configuration
public class StartProcess {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BusinessWorkflowMappingRepository businessWorkflowMappingRepository;

    @MessagingGateway
    public interface ProcessStarter {

        @Gateway(requestChannel = "eventStartFlow.input")
        void placeBE(BusinessEvent businessEvent);

    }

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Bean
    public IntegrationFlow eventStartFlow() {

        return (IntegrationFlowDefinition<?> f) -> f
                .channel((Channels c) -> c.executor(Executors.newCachedThreadPool()))
                .handle(m -> {
                            BusinessEvent businessEvent = (BusinessEvent) m.getPayload();
                            BusinessWorkflowMapping mapping = businessWorkflowMappingRepository.getMapping(businessEvent);
                            startProcess(businessEvent, mapping);
                        }
                );

    }


    class StartProcessRequest {
        String processDefinitionKey;
        String businessKey;
        List<Variable> variables;

        public String getProcessDefinitionKey() {
            return processDefinitionKey;
        }

        public void setProcessDefinitionKey(String processDefinitionKey) {
            this.processDefinitionKey = processDefinitionKey;
        }

        public String getBusinessKey() {
            return businessKey;
        }

        public void setBusinessKey(String businessKey) {
            this.businessKey = businessKey;
        }

        public List<Variable> getVariables() {
            return variables;
        }

        public void setVariables(List<Variable> variables) {
            this.variables = variables;
        }
    }

    class Variable {
        String name;
        String value;

        public Variable(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public void startProcess(BusinessEvent businessEvent, BusinessWorkflowMapping mapping) {

        String url = "http://localhost:8080/runtime/process-instances";
        StartProcessRequest requestJson = new StartProcessRequest();
        requestJson.setBusinessKey(UUID.randomUUID().toString());
        requestJson.setProcessDefinitionKey("oneTaskProcess");
        List<Variable> vs = new ArrayList<>();
        vs.add(new Variable("myVar", "variable value"));

        HttpHeaders headers = new HttpHeaders();
        getHttpHeadersWithUserCredentials(headers);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<StartProcessRequest> entity = new HttpEntity<>(requestJson, headers);
        String answer = restTemplate.postForObject(url, entity, String.class);
        System.out.println(answer);
    }


    private HttpHeaders getHttpHeadersWithUserCredentials(HttpHeaders headers) {

        String username = "admin";
        String password = "admin";

        String combinedUsernamePassword = username + ":" + password;
        byte[] base64Token = Base64.getEncoder().encode(combinedUsernamePassword.getBytes());
        String base64EncodedToken = new String(base64Token);
        //adding Authorization header for HTTP Basic authentication
        headers.add("Authorization", "Basic  " + base64EncodedToken);

        return headers;
    }
}
