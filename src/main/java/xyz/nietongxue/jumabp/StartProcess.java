package xyz.nietongxue.jumabp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.Executors;

@Configuration
public class StartProcess {

    @Autowired
    private RestTemplate restTemplate;

    @MessagingGateway
    public interface ProcessStarter {

        @Gateway(requestChannel = "eventStartFlow.input")
        void placeBE(BE be);

    }
    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }
    @Bean
    public IntegrationFlow eventStartFlow() {
        return f -> f
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .handle(m ->
                        startProcess()
                );
    }

//    @Bean
//    public MappingJackson2HttpMessageConverter converter(){
//        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        //设置日期格式
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd");
//        objectMapper.setDateFormat(smt);
//        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
//        //设置中文编码格式
//        List<MediaType> list = new ArrayList<MediaType>();
//        list.add(MediaType.APPLICATION_JSON_UTF8);
//        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
//        return mappingJackson2HttpMessageConverter;
//    }

    class StartProcessRequest{
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
    class Variable{
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
    public void startProcess() {

        String url = "http://localhost:8080/runtime/process-instances";
        StartProcessRequest requestJson = new StartProcessRequest();
        requestJson.setBusinessKey(UUID.randomUUID().toString());
        requestJson.setProcessDefinitionKey("oneTaskProcess");
        List<Variable> vs = new ArrayList<>();
        vs.add(new Variable("myVar","variable value"));

//                "{\n" +
//                "   \"processDefinitionKey\":\"oneTaskProcess\",\n" +
//                "   \"businessKey\":\"myBusinessKey\",\n" +
//                "   \"variables\": [\n" +
//                "      {\n" +
//                "        \"name\":\"myVar\",\n" +
//                "        \"value\":\"This is a variable\"\n" +
//                "      }\n" +
//                "   ]\n" +
//                "}";
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
