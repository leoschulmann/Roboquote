package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.BundleItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemId;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.services.ItemBundleDtoConverter;
import com.leoschulmann.roboquote.quoteservice.dto.DistinctTermsDto;
import com.leoschulmann.roboquote.quoteservice.dto.QuoteDto;
import com.leoschulmann.roboquote.quoteservice.dto.XlsxDataObject;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.services.QuoteDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HttpRestService {

    @Value("${itemservice.url}")
    private String itemUrl;

    @Value("${bundleservice.url}")
    private String bundleUrl;

    @Value("${quoteservice.url}")
    private String quoteUrl;

    @Value("${namingservice.url}")
    private String nameUrl;

    @Value("${xlsxservice.url}")
    private String downloadUrl;

    private final RestTemplate restTemplate;
    private final AuthService auth;
    private final ItemBundleDtoConverter itemBundleDtoConverter;
    private final QuoteDtoConverter quoteDtoConverter;
    private final ConverterService converterService;

    public List<Item> getAllItems() throws ServerCommunicationException {
        RequestEntity<Void> request = RequestEntity.get(URI.create(itemUrl))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        try {
            ItemDto[] arr = restTemplate.exchange(request, ItemDto[].class).getBody();
            return Arrays.stream(arr).map(itemBundleDtoConverter::convertToItem).collect(Collectors.toList());
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    public DistinctTermsDto getDistinctTerms() {
        RequestEntity<Void> request = RequestEntity.get(URI.create(quoteUrl + "terms"))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        return restTemplate.exchange(request, DistinctTermsDto.class).getBody();
    }

    public Item getItem(int id) {
        RequestEntity<Void> request = RequestEntity.get(URI.create(itemUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        ItemDto dto = restTemplate.exchange(request, ItemDto.class).getBody();
        return itemBundleDtoConverter.convertToItem(dto); //todo handle nullable
    }

    public List<Item> getItemsByIds(int... ids) {
        List<ItemId> idList = Arrays.stream(ids).mapToObj(ItemId::new).collect(Collectors.toList());
        RequestEntity<List<ItemId>> request = RequestEntity.post(URI.create(itemUrl + "multiple"))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).body(idList);
        ItemDto[] arr = restTemplate.exchange(request, ItemDto[].class).getBody();
        if (arr == null || arr.length == 0) throw new RuntimeException("work in progress!"); //todo replace stub
        return Arrays.stream(arr).map(itemBundleDtoConverter::convertToItem).collect(Collectors.toList());
    }

    @Transactional
    public void saveItem(Item item) { //todo return id???
        ItemDto dto = itemBundleDtoConverter.convertToItemDto(item);
        RequestEntity<ItemDto> request = RequestEntity.post(URI.create(itemUrl))
                .headers(auth.provideHttpHeadersWithCredentials()).body(dto);
        restTemplate.exchange(request, Object.class);
    }

    public void deleteItem(int id) {
        RequestEntity<Void> request = RequestEntity.delete(URI.create(itemUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).build();
        restTemplate.exchange(request, Object.class);
    }

    @Transactional
    public void updateItem(Item item) {
        int id = item.getId();
        ItemDto dto = itemBundleDtoConverter.convertToItemDto(item);
        RequestEntity<ItemDto> request = RequestEntity.put(URI.create(itemUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).body(dto);
        restTemplate.exchange(request, Object.class);
    }

    public List<Bundle> getAllBundlesNamesAndIds() {
        RequestEntity<Void> request = RequestEntity.get(URI.create(bundleUrl))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        BundleDto[] arr = restTemplate.exchange(request, BundleDto[].class).getBody();
        if (arr == null || arr.length == 0) throw new RuntimeException("work in progress!"); //todo replace stub

        return Arrays.stream(arr).map(dto -> {
            Bundle b = new Bundle();
            b.setNameRus(dto.getName());
            b.setId(dto.getId());
            return b;
        }).collect(Collectors.toList());

    }

    public Bundle getBundleById(int id) {
        RequestEntity<Void> request = RequestEntity.get(URI.create(bundleUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        BundleDto dto = restTemplate.exchange(request, BundleDto.class).getBody();
        if (dto == null) throw new RuntimeException("work in progress!"); //todo replace stub
        Bundle bundle = new Bundle();
        bundle.setId(dto.getId());
        bundle.setNameRus(dto.getName()); //todo i8n

        int[] itemIds = dto.getItems().stream().mapToInt(BundleItemDto::getItemId).toArray(); //ordered
        List<Item> items = this.getItemsByIds(itemIds);                                       //unordered
        List<BundledPosition> positions = Arrays.stream(itemIds).mapToObj(itemid -> {
                    int qty = dto.getItems().stream().filter(idto -> idto.getItemId() == itemid).findAny().get().getQty();
                    Item item = items.stream().filter(item1 -> item1.getId() == itemid).findFirst().get();
                    return new BundledPosition(qty, item);
                }
        ).collect(Collectors.toList());


        bundle.setPositions(positions);
        return bundle;
    }

    public void saveBundle(Bundle bundle) { //todo return id???
        BundleDto dto = itemBundleDtoConverter.convertFromBundle(bundle);
        RequestEntity<BundleDto> request = RequestEntity.post(URI.create(bundleUrl))
                .headers(auth.provideHttpHeadersWithCredentials()).body(dto);
        restTemplate.exchange(request, Object.class);
    }

    public void updateBundle(Bundle bundle) {
        BundleDto dto = itemBundleDtoConverter.convertFromBundle(bundle);
        RequestEntity<BundleDto> request = RequestEntity.put(URI.create(bundleUrl + bundle.getId()))
                .headers(auth.provideHttpHeadersWithCredentials()).body(dto);
        restTemplate.exchange(request, Object.class);
    }

    public void deleteBundle(int id) {
        RequestEntity<Void> request = RequestEntity.delete(URI.create(bundleUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).build();
        restTemplate.exchange(request, Object.class);
    }

    public List<ItemPosition> batchCopyPositions(List<ItemPosition> positions) {
        int[] itemIds = positions.stream().mapToInt(ItemPosition::getItemId).toArray(); //ordered
        List<Item> items = getItemsByIds(itemIds);                                      //unordered
        return Arrays.stream(itemIds).mapToObj(itemid -> {
            int qty = positions.stream().filter(ip -> ip.getItemId() == itemid).findFirst().get().getQty();
            Item i = items.stream().filter(item -> item.getId() == itemid).findFirst().get();
            return converterService.convertItemToItemPosition(i, qty);
        }).collect(Collectors.toList());
    }

    public List<Quote> findAllQuotes(boolean findCancelled) throws ServerCommunicationException {
        URI uri = findCancelled ? URI.create(quoteUrl) : URI.create(quoteUrl + "uncancelled");

        RequestEntity<Void> request = RequestEntity.get(uri)
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();

        try {
            QuoteDto[] arr = restTemplate.exchange(request, QuoteDto[].class).getBody();
            return Arrays.stream(arr).map(quoteDtoConverter::convertDtoToMinimalQuote).collect(Collectors.toList());
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    public Quote getQuoteById(int id) throws ServerCommunicationException {
        RequestEntity<Void> request = RequestEntity.get(URI.create(quoteUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        try {
            ResponseEntity<QuoteDto> response = restTemplate.exchange(request, QuoteDto.class);
            QuoteDto dto = response.getBody();
            return quoteDtoConverter.convertDtoToQuote(dto);
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    public XlsxDataObject persistAndReturnData(QuoteDto dto) throws ServerCommunicationException {
        String serialNumber = dto.getNumber();

        if (serialNumber == null || serialNumber.isBlank()) {
            serialNumber = getNameFromService();
        }

        Integer quoteVersion = getVersionFromService(dto.getNumber());

        dto.setNumber(serialNumber);
        dto.setVersion(quoteVersion);


        RequestEntity<QuoteDto> request = RequestEntity.post(URI.create(quoteUrl))
                .headers(auth.provideHttpHeadersWithCredentials()).contentType(MediaType.APPLICATION_JSON)
                .body(dto);

        try {
            return restTemplate.exchange(request, XlsxDataObject.class).getBody();
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    public XlsxDataObject getDataObjectForId(int id) throws ServerCommunicationException {
        RequestEntity<Void> request = RequestEntity.get(URI.create(downloadUrl + id))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();

        try {
            return restTemplate.exchange(request, XlsxDataObject.class).getBody();
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    private String getNameFromService() throws ServerCommunicationException {
        RequestEntity<Void> request = RequestEntity.get(URI.create(nameUrl))
                .headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        try {
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    private Integer getVersionFromService(String serial) throws ServerCommunicationException {
        RequestEntity<Void> request = RequestEntity.get(URI.create(nameUrl + serial)).accept(MediaType.APPLICATION_JSON)
                .headers(auth.provideHttpHeadersWithCredentials()).build();
        try {
            ResponseEntity<Integer> response = restTemplate.exchange(request, Integer.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    public void addComment(int id, String value) throws ServerCommunicationException {
        RequestEntity<String> request = RequestEntity.post(URI.create(quoteUrl + "comment/" + id))
                .headers(auth.provideHttpHeadersWithCredentials()).contentType(MediaType.APPLICATION_JSON)
                .body(value);
        try {
            restTemplate.exchange(request, String.class);
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    public void setCancelled(int id, boolean cancel) throws ServerCommunicationException {
        RequestEntity<String> request = RequestEntity.post(URI.create(quoteUrl + "cancel/" + id))
                .headers(auth.provideHttpHeadersWithCredentials()).contentType(MediaType.APPLICATION_JSON)
                .body(String.valueOf(cancel));
        try {
            restTemplate.exchange(request, String.class);
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }

    public List<Quote> findAllQuotesForItemId(int itemId) throws ServerCommunicationException {
        RequestEntity<Void> request = RequestEntity.get(URI.create(quoteUrl + "foritem/" + itemId)).
                headers(auth.provideHttpHeadersWithCredentials()).accept(MediaType.APPLICATION_JSON).build();
        try {
            ResponseEntity<QuoteDto[]> responseEntity = restTemplate.exchange(request, QuoteDto[].class);
            QuoteDto[] arr = responseEntity.getBody();

            return Arrays.stream(arr).map(quoteDtoConverter::convertDtoToMinimalQuote).collect(Collectors.toList());
        } catch (RestClientResponseException e) {
            throw new ServerCommunicationException(e.getResponseBodyAsString());
        }
    }
}
