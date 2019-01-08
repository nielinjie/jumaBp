package xyz.nietongxue.jumabp;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
class MappingRepositoryConf{
    @Bean
    BusinessWorkflowMappingRepository repository(){
        return new FakeBusinessWorkflowMappingRepository();
    }
}

interface BusinessWorkflowMappingRepository{
    BusinessWorkflowMapping getMapping(BE be);
}

class FakeBusinessWorkflowMappingRepository implements   BusinessWorkflowMappingRepository{
    @Override
    public BusinessWorkflowMapping getMapping(BE be) {
        Map<String,String> vaMap = new HashMap<>();
        vaMap.put("businessPropertyNameA","workflowVariableNameA");
        BusinessWorkflowMapping bwm = new BusinessWorkflowMapping(
                vaMap
                ,
                "oneTaskWorkflow"
        );
        return bwm;
    }
}

 class BusinessWorkflowMapping {
    private Map<String, String> variableNamesMapping;
    private String workflowKey;

     public BusinessWorkflowMapping(Map<String, String> variableNamesMapping, String workflowKey) {
         this.variableNamesMapping = variableNamesMapping;
         this.workflowKey = workflowKey;
     }

     public Map<String, String> getVariableNamesMapping() {
         return variableNamesMapping;
     }

     public void setVariableNamesMapping(Map<String, String> variableNamesMapping) {
         this.variableNamesMapping = variableNamesMapping;
     }

     public String getWorkflowKey() {
         return workflowKey;
     }

     public void setWorkflowKey(String workflowKey) {
         this.workflowKey = workflowKey;
     }
 }
