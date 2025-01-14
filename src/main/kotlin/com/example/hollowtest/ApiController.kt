package com.example.hollowtest

import com.navercorp.fixturemonkey.FixtureMonkey
import net.jqwik.api.Arbitraries
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RestController
@CrossOrigin(origins = ["http://localhost:5173"])
class ApiController(
    private val fixtureMonkey: FixtureMonkey

) {
    @GetMapping("/centers")
    fun centerList(): List<CenterResponse> {
        return listOf(
            CenterResponse("ANS1"),
            CenterResponse("ANS2"),
            CenterResponse("ANS3"),
            CenterResponse("ANS4"),
            CenterResponse("ANS5")
        )
    }

    @GetMapping("/hide-status")
    fun hideStatusList(): List<HideStatusResponse> {
        return HideStatus.entries.map { HideStatusResponse(it.name) }
    }


    @PostMapping("/hide/search")
    fun searchHide(@RequestBody hideSearchRequest: HideSearchRequest): Page<HideSearchResponse> {

        val hideCenterArbitrary = fixtureMonkey.giveMeBuilder(HideCenterResponse::class.java)
            .set(
                HideCenterResponse::hideCenterCode.name,
                Arbitraries.of("ANS1", "ANS2", "ANS3", "ANS4", "ANS5", "ANS6", "ANS7", "ANS8", "ANS9", "ANS10")
            )
            .set(
                HideCenterResponse::hideInventory.name,
                Arbitraries.longs().between(1, 1000)
            )
            .set(
                HideCenterResponse::hideStatus.name,
                Arbitraries.of("HIDING", "HIDE", "UNHIDING", "UNHIDE")
            ).sampleList(10)


        val allData = fixtureMonkey.giveMeBuilder(HideSearchResponse::class.java)
            .set(
                HideSearchResponse::skuId.name,
                Arbitraries.longs().between(1, 101)
            )
            .set(HideSearchResponse::startDate.name, Arbitraries.just(LocalDate.now()))
            .set(HideSearchResponse::endDate.name, Arbitraries.just(LocalDate.now()))
            .set(HideSearchResponse::createdAt.name, Arbitraries.just(LocalDate.now()))
            .set(HideSearchResponse::modifiedAt.name, Arbitraries.just(LocalDate.now()))
            .set(
                HideSearchResponse::reason.name,
                Arbitraries.strings()
                    .alpha()
                    .ofMinLength(3)
                    .ofMaxLength(8)
                    .map { "test-$it" }
            )
            .set(
                HideSearchResponse::createdId.name,
                Arbitraries.strings()
                    .alpha()
                    .ofMinLength(3)
                    .ofMaxLength(8)
                    .map { "test-$it" }
            )
            .set(
                HideSearchResponse::modifiedId.name,
                Arbitraries.strings()
                    .alpha()
                    .ofMinLength(3)
                    .ofMaxLength(8)
                    .map { "test-$it" }
            )
            //
            .set(HideSearchResponse::minimumInventory.name, Arbitraries.longs().between(1, 10))
            .set(HideSearchResponse::nationalDocQty.name, Arbitraries.longs().between(1, 10))
            .set(HideSearchResponse::nationalDoc.name, Arbitraries.doubles().between(1.0, 10.0))
            .set(
                HideSearchResponse::monitoringCenterCodeList.name,
                Arbitraries.of("ANS1", "ANS2", "ANS3", "ANS4", "ANS5", "ANS6", "ANS7", "ANS8", "ANS9", "ANS10")
                    .list()
                    .uniqueElements()
                    .ofMinSize(1)
                    .ofMaxSize(10)
            )
            .set(
                HideSearchResponse::hideCenterList.name,
                Arbitraries.of(hideCenterArbitrary).list().ofMaxSize(5)
            )
            .sampleList(100)

        val filteredData = allData.filter { response ->

            val matchSkuId = if (hideSearchRequest.skuId.isNotEmpty()) {
                response.skuId in hideSearchRequest.skuId
            } else {
                true
            }

            val matchCenterCode = if (hideSearchRequest.centerCode.isNotEmpty()) {
                response.monitoringCenterCodeList.any { it in hideSearchRequest.centerCode }
            } else {
                true
            }

            matchSkuId && matchCenterCode
        }

        val pageSize = hideSearchRequest.pageSize
        val pageNumber = hideSearchRequest.pageNumber

        val fromIndex = pageNumber * pageSize
        val toIndex = (fromIndex + pageSize).coerceAtMost(filteredData.size)

        val content = if (fromIndex < filteredData.size) {
            filteredData.subList(fromIndex, toIndex)
        } else {
            emptyList()
        }

        val pageable = PageRequest.of(pageNumber, pageSize)

        return PageImpl(content, pageable, filteredData.size.toLong())
    }

    @GetMapping("/hide/history")
    fun searchHistory(@RequestParam skuId: Long): List<HideSearchResponse> {

        val hideCenterArbitrary = fixtureMonkey.giveMeBuilder(HideCenterResponse::class.java)
            .set(
                HideCenterResponse::hideCenterCode.name,
                Arbitraries.of("ANS1", "ANS2", "ANS3", "ANS4", "ANS5", "ANS6", "ANS7", "ANS8", "ANS9", "ANS10")
            )
            .set(
                HideCenterResponse::hideInventory.name,
                Arbitraries.longs().between(1, 1000)
            )
            .set(
                HideCenterResponse::hideStatus.name,
                Arbitraries.of("HIDING", "HIDE", "UNHIDING", "UNHIDE")
            ).sampleList(1)


        return fixtureMonkey.giveMeBuilder(HideSearchResponse::class.java)
            .set(
                HideSearchResponse::skuId.name,
                Arbitraries.longs().between(1, 101)
            )
            .set(HideSearchResponse::startDate.name, Arbitraries.just(LocalDate.now()))
            .set(HideSearchResponse::endDate.name, Arbitraries.just(LocalDate.now()))
            .set(HideSearchResponse::createdAt.name, Arbitraries.just(LocalDate.now()))
            .set(HideSearchResponse::modifiedAt.name, Arbitraries.just(LocalDate.now()))
            .set(
                HideSearchResponse::reason.name,
                Arbitraries.strings()
                    .alpha()
                    .ofMinLength(3)
                    .ofMaxLength(8)
                    .map { "test-$it" }
            )
            .set(
                HideSearchResponse::createdId.name,
                Arbitraries.strings()
                    .alpha()
                    .ofMinLength(3)
                    .ofMaxLength(8)
                    .map { "test-$it" }
            )
            .set(
                HideSearchResponse::modifiedId.name,
                Arbitraries.strings()
                    .alpha()
                    .ofMinLength(3)
                    .ofMaxLength(8)
                    .map { "test-$it" }
            )
            //
            .set(HideSearchResponse::minimumInventory.name, Arbitraries.longs().between(1, 10))
            .set(HideSearchResponse::nationalDocQty.name, Arbitraries.longs().between(1, 10))
            .set(HideSearchResponse::nationalDoc.name, Arbitraries.doubles().between(1.0, 10.0))
            .set(
                HideSearchResponse::monitoringCenterCodeList.name,
                Arbitraries.of("ANS1", "ANS2", "ANS3", "ANS4", "ANS5", "ANS6", "ANS7", "ANS8", "ANS9", "ANS10")
                    .list()
                    .uniqueElements()
                    .ofMinSize(1)
                    .ofMaxSize(10)
            )
            .set(
                HideSearchResponse::hideCenterList.name,
                Arbitraries.of(hideCenterArbitrary).list().ofMaxSize(5)
            )
            .sampleList(20)
            .sortedByDescending { it.modifiedAt }
    }

}

data class HideSearchRequest(
    val pageSize: Int,
    val pageNumber: Int,
    val skuId: List<Long>,
    val centerCode: List<String>,
    val hideStatusList: List<String>
)

data class HideSearchResponse(
    val skuId: Long,
    val hideCenterList: List<HideCenterResponse>,
    val monitoringCenterCodeList: List<String>,
    val minimumInventory: Long,
    val nationalDocQty: Long,
    val nationalDoc: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: String,
    val createdId: String,
    val createdAt: LocalDate,
    val modifiedId: String,
    val modifiedAt: LocalDate
)

data class HideCenterResponse(
    val hideCenterCode: String,
    val hideInventory: Long,
    val hideStatus: String
)

data class CenterResponse(
    val centerId: String
)

data class HideStatusResponse(
    val status: String
)

data class SearchConditionRequest(
    val skuIds: List<String>,
    val hideCenterCodes: List<String>,
    val hideStatus: List<String>,
)

enum class HideStatus(
    private val userDescription: String, private val display: Boolean
) {
    HIDING("HIDING", true), HIDE("HIDE", true), UNHIDING("UNHIDING", true), UNHIDING_STAGE(
        "UNHIDING_STAGE", false
    ),
    UNHIDE("UNHIDE", false),
}