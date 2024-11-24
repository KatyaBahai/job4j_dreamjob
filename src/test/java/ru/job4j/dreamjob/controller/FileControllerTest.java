package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {
    private final FileService fileService = mock(FileService.class);
    private final FileController fileController = new FileController(fileService);
    private final MultipartFile testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});

    @Test
    public void whenGetFileByIdThenFound() throws IOException {
        FileDto fileDto = new FileDto("fileName", testFile.getBytes());
        int id = 1;
        ArgumentCaptor<Integer> fileIdCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.getFileById(fileIdCaptor.capture())).thenReturn(Optional.of(fileDto));

        ResponseEntity<?> expectedResponseEntity = fileController.getById(id);

        assertThat(HttpStatus.OK).isEqualTo(expectedResponseEntity.getStatusCode());
        assertThat(fileDto.getContent()).isEqualTo(expectedResponseEntity.getBody());
    }
}