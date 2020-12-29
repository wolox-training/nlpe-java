package com.wolox.training.service;

import com.wolox.training.dto.BookDTO;
import com.wolox.training.exception.BookNotFoundException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenLibraryService {

    @Value("${external.api.url}")
    private String apiUrl;

    public BookDTO bookInfo(String isbn) throws IOException, BookNotFoundException {

        HttpGet get = new HttpGet(apiUrl + "?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data");
        HttpResponse response = getClient().execute(get);
        JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
        if (json.isEmpty()) {
            throw new BookNotFoundException("Book not found");
        }

        return new BookDTO(isbn, json.getJSONObject("ISBN:" + isbn));
    }

    private HttpClient getClient() {
        return HttpClientBuilder.create().build();
    }
}
