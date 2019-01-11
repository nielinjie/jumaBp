package xyz.nietongxue.jumabp

interface BusinessWorkflowMappingRepository {
    fun getMapping(businessEvent: BusinessEvent): BusinessWorkflowMapping
}

data class BusinessWorkflowMapping(
        val variableNamesMapping: Map<String, String>,
        val workflowKey: String,
        val businessKeyName: String
)
