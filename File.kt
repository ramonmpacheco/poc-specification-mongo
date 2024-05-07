import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*

data class Document(
    val nome: String,
    val ano: Int,
    val status: String
)

interface DocumentSpecification {
    fun toQuery(): Query
}

class BaseDocumentSpecification(
    private val nome: String,
    private val ano: Int,
    private val status: String?
) : DocumentSpecification {
    override fun toQuery(): Query {
        val query = Query()
        query.addCriteria(Criteria.where("nome").`is`(nome).and("ano").`is`(ano))
        
        status?.let {
            if (it != "all") {
                query.addCriteria(Criteria.where("status").`is`(status))
            }
        }
        
        return query
    }
}

@RestController
@RequestMapping("/documents")
class DocumentController(private val mongoTemplate: MongoTemplate) {

    @GetMapping
    fun getDocument(
        @RequestParam nome: String,
        @RequestParam ano: Int,
        @RequestParam(required = false) status: String?
    ): Document? {
        val specification = BaseDocumentSpecification(nome, ano, status)
        val query = specification.toQuery()
        return mongoTemplate.findOne(query, Document::class.java)
    }
}
