package xyz.nietongxue.jumabp


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class MappingRepositoryConf {
    @Bean
    open fun repository(): BusinessWorkflowMappingRepository {
        return FakeBusinessWorkflowMappingRepository()
    }
}

class FakeBusinessWorkflowMappingRepository : BusinessWorkflowMappingRepository {
    override fun getMapping(businessEvent: BusinessEvent) =
            BusinessWorkflowMapping(
                    mapOf("businessPropertyNameA" to "workflowVariableNameA"),
                    "oneTaskWorkflow", "bkPropertyName"
            )
}