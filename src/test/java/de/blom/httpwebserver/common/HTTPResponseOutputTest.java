package de.blom.httpwebserver.common;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintWriter;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HTTPResponseOutputTest {

    @Spy
    private HTTPResponseOutput httpResponseOutput;

    @Mock
    private PrintWriter out;

    private Date currentDate = new Date();

    @Before
    public void setup(){
        when(this.httpResponseOutput.getCurrentDate()).thenReturn(this.currentDate);
    }

    @Test
    public void expectToCall200Method() {
        this.httpResponseOutput.writeResponse(HttpStatus.SC_OK, this.out);

        verify(this.httpResponseOutput).write200Response(this.out);
    }

    @Test
    public void expectToCall404Method() {
        this.httpResponseOutput.writeResponse(HttpStatus.SC_NOT_FOUND, this.out);

        verify(this.httpResponseOutput).write404Response(this.out);
    }

    @Test
    public void expectToOutputCorrect200Status() {
        this.httpResponseOutput.write200Response(out);

        verify(this.out).println(eq("HTTP/1.1 200 OK"));
        verify(this.out).println(eq("Server: Java HTTP Server"));
        verify(this.out).println(eq("Date: " + this.currentDate));
    }

    @Test
    public void expectToOutputCorrect404Status() {
        this.httpResponseOutput.write404Response(out);

        verify(this.out).println(eq("HTTP/1.1 404 File Not Found"));
        verify(this.out).println(eq("Server: Java HTTP Server"));
        verify(this.out).println(eq("Date: " + this.currentDate));
    }


}