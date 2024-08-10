package walletbackend.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import walletbackend.entities.Offering
import walletbackend.entities.Pfi
import walletbackend.repositories.PfiRepository
import java.util.*

@Component
class DBInitializer(private val pfiRepository: PfiRepository, private val objectMapper: ObjectMapper) {

    @PostConstruct
    fun initializeDB() {
        try {
            loadPfisToDB()
        }
        catch (e: Exception) {
            log.error("Migration Failed: {}", e.message)
        }
    }

    private fun loadPfisToDB(){

        val pfis = convertJsonToPfis("pfi.json")
        val savedPfis = pfiRepository.saveAll(pfis)
        log.info("Loaded {} pfis successfully", savedPfis.count())
    }

    private fun convertJsonToPfis(fileName: String): List<Pfi> {
        val file = ResourceUtils.getFile("classpath:$fileName")
        val typeRef = object : TypeReference<ArrayList<Pfi>>() {}
        return objectMapper.readValue(file.readText(), typeRef)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DBInitializer::class.java)
    }

}