package de.blom.httpwebserver.adapter.inbound.http;

import de.blom.httpwebserver.adapter.inbound.http.commons.HttpRequest;
import de.blom.httpwebserver.adapter.inbound.http.commons.ResponseWriter;
import de.blom.httpwebserver.domain.wall.WallContentService;
import de.blom.httpwebserver.exception.WrongContentTypeException;
import de.blom.httpwebserver.exception.InvalidDataException;
import de.blom.httpwebserver.representation.fileserver.DirectoryRequestDto;
import de.blom.httpwebserver.domain.fileserver.DirectoryService;
import de.blom.httpwebserver.representation.fileserver.FileRequestDto;
import de.blom.httpwebserver.representation.wall.WallEntryInboundDto;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HttpAdapterTest {


    private static final String INDEX_HTML = "/index.html";
    private static final String WALL_ENTRIES_URI = "/wall_entries";
    private static final String ROOT_DIRECTORY = "/";


    private HttpAdapter httpAdapter;

    @Mock
    private BufferedReader in;

    @Mock
    private PrintWriter httpResponseHeader;

    @Mock
    private ResponseWriter responseWriter;

    @Mock
    private BufferedOutputStream httpResponseBody;

    @Mock
    private Socket mockedSocket;

    @Mock
    private DirectoryService directoryService;

    @Mock
    private DirectoryRequestDto mockedDirectoryRequestDtoNotFound;

    @Mock
    private DirectoryRequestDto mockedDirectoryRequestDtoFound;

    @Mock
    private FileRequestDto mockedFileRequestDtoNotFound;

    @Mock
    private FileRequestDto mockedFileRequestDtoFound;

    @Mock
    private WallContentService wallContentService;

    @Before
    public void setup() {

        this.httpAdapter = new HttpAdapter(this.mockedSocket, this.responseWriter, this.directoryService, this.wallContentService);
        this.httpAdapter = Mockito.spy(this.httpAdapter);

        when(mockedDirectoryRequestDtoNotFound.getFound()).thenReturn(false);

        when(mockedDirectoryRequestDtoFound.getFound()).thenReturn(true);

        when(mockedFileRequestDtoFound.getFound()).thenReturn(false);

        when(mockedFileRequestDtoFound.getFound()).thenReturn(true);
    }


    @Test
    public void expectToCallHandleGetMethod() throws IOException {
        HttpRequest incomingRequest = new HttpRequest("GET", INDEX_HTML, null, null);
        doNothing().when(this.httpAdapter).handleDirectoryServerRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);

        this.httpAdapter.handleHttpMethod(this.httpResponseHeader, this.httpResponseBody, incomingRequest);

        verify(this.httpAdapter).handleDirectoryServerRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);
    }

    @Test
    public void expectToCallHandlePostMethod() throws IOException {
        HttpRequest incomingRequest = new HttpRequest("POST", WALL_ENTRIES_URI, null, null);
        doNothing().when(this.httpAdapter).handlePostRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);

        this.httpAdapter.handleHttpMethod(this.httpResponseHeader, this.httpResponseBody, incomingRequest);

        verify(this.httpAdapter).handlePostRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);
    }

    @Test
    public void expectHandleNotSupportedMethodWith501() throws IOException {
        HttpRequest incomingRequest = new HttpRequest("PUT", WALL_ENTRIES_URI, null, null);
        this.httpAdapter.handleHttpMethod(this.httpResponseHeader, this.httpResponseBody, incomingRequest);

        verify(this.responseWriter).respondeWith501(this.httpResponseHeader, this.httpResponseBody);
    }

    @Test
    public void expectToCallDirectoryHandling() throws IOException {
        HttpRequest incomingRequest = new HttpRequest("GET", ROOT_DIRECTORY, null, null);
        when(this.directoryService.handleDirectoryRequest(ROOT_DIRECTORY)).thenReturn(this.mockedDirectoryRequestDtoFound);

        this.httpAdapter.handleDirectoryServerRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);

        verify(this.httpAdapter).handleDirectoryRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);
    }

    @Test
    public void expectToCallFileHandling() throws IOException {
        HttpRequest incomingRequest = new HttpRequest("GET", INDEX_HTML, null, null);
        when(this.directoryService.handleFileRequest(INDEX_HTML)).thenReturn(mockedFileRequestDtoFound);

        this.httpAdapter.handleDirectoryServerRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);

        verify(this.httpAdapter).handleFileRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);
    }

    @Test
    public void expectToCallFileHandlingNotFoundWithoutHttpResponseBody() throws IOException {
        HttpRequest incomingRequest = new HttpRequest("HEAD", INDEX_HTML, null, null);
        when(this.directoryService.handleFileRequest(INDEX_HTML)).thenReturn(this.mockedFileRequestDtoNotFound);

        this.httpAdapter.handleFileRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);

        verify(this.responseWriter).respondeWith404(this.httpResponseHeader, null);
    }


    @Test
    public void expectToCallFileHandlingWithoutHttpResponseBody() throws IOException {
        when(this.directoryService.handleFileRequest(INDEX_HTML)).thenReturn(this.mockedFileRequestDtoFound);
        HttpRequest incomingRequest = new HttpRequest("HEAD", INDEX_HTML, null, null);

        this.httpAdapter.handleFileRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);

        verify(this.responseWriter).writeHttpResponse(this.mockedFileRequestDtoFound, this.httpResponseHeader, null);
    }

    @Test
    public void expectToCallDirectoryHandling404WithoutHttpResponseBody() throws IOException {
        HttpRequest incomingRequest = new HttpRequest("HEAD", ROOT_DIRECTORY, null, null);
        when(this.directoryService.handleDirectoryRequest(ROOT_DIRECTORY)).thenReturn(this.mockedDirectoryRequestDtoNotFound);

        this.httpAdapter.handleDirectoryRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);

        verify(this.responseWriter).respondeWith404(this.httpResponseHeader, null);
    }

    @Test
    public void expectToCallDirectoryHandlingWithoutHttpResponseBody() throws IOException {
        HttpRequest incomingRequest = new HttpRequest("HEAD", ROOT_DIRECTORY, null, null);
        when(this.directoryService.handleDirectoryRequest(ROOT_DIRECTORY)).thenReturn(this.mockedDirectoryRequestDtoFound);

        this.httpAdapter.handleDirectoryRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);

        verify(this.responseWriter).writeHttpResponse(this.mockedDirectoryRequestDtoFound, this.httpResponseHeader, null);
    }

    @Test
    public void expectToCallDirectoryServiceWithSuitableDto() {
        String rawBody = "{\n" +
                "\t\"author\": \"Max Mustermann\",\n" +
                "\t\"text\": \"Lorem ipsum dolor\"\n" +
                "}";

        Map<String, String> correctHeader = new HashMap<>();
        correctHeader.put("Content-Type", "application/json");

        WallEntryInboundDto expectedIncomingDto = new WallEntryInboundDto("Max Mustermann", "Lorem ipsum dolor");
        HttpRequest incomingRequest = new HttpRequest("POST", WALL_ENTRIES_URI, correctHeader, rawBody);


        this.httpAdapter.handlePostRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);


        ArgumentCaptor<WallEntryInboundDto> varArgs = ArgumentCaptor.forClass(WallEntryInboundDto.class);
        verify(this.wallContentService).createNewWallEntry(varArgs.capture());

        assertThat(varArgs.getValue(), Matchers.samePropertyValuesAs(expectedIncomingDto));
    }

    @Test(expected = WrongContentTypeException.class)
    public void expectToThrowBadRequestException_contentTypeMissing() {
        Map<String, String> header = new HashMap<>();

        HttpRequest incomingRequest = new HttpRequest("POST", WALL_ENTRIES_URI, header, "");

        this.httpAdapter.handlePostRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);
    }

    @Test(expected = InvalidDataException.class)
    public void expectToThrowBadRequestException_jsonContentIsWrong() {
        String rawBody = "{\n" +
                "\t\"invalid\": \"Max Mustermann\",\n" +
                "\t\"invalid_2\": \"Lorem ipsum dolor\"\n" +
                "}";

        Map<String, String> correctHeader = new HashMap<>();
        correctHeader.put("Content-Type", "application/json");

        HttpRequest incomingRequest = new HttpRequest("POST", WALL_ENTRIES_URI, correctHeader, rawBody);


        this.httpAdapter.handlePostRequest(incomingRequest, this.httpResponseHeader, this.httpResponseBody);
    }


}