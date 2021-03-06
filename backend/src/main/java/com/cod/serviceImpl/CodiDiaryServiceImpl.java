package com.cod.serviceImpl;

import com.cod.configuration.ValidationCheck;
import com.cod.dao.CodiDiaryRepository;
import com.cod.dto.codidiary.createcodidiary.CreateCodiDiaryInput;
import com.cod.dto.codidiary.selectcodidiary.SelectCodiDiaryInput;
import com.cod.dto.codidiary.selectcodidiary.SelectCodiDiaryOutput;
import com.cod.dto.codidiary.updatecodidiary.UpdateCodiDiaryInput;
import com.cod.entity.CodiDiary;
import com.cod.entity.User;
import com.cod.response.PageResponse;
import com.cod.response.Response;
import com.cod.service.CodiDiaryService;
import com.cod.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.cod.response.ResponseStatus.*;

@Service("CodiDiaryService")
@RequiredArgsConstructor
@Slf4j
public class CodiDiaryServiceImpl implements CodiDiaryService {

    private final CodiDiaryRepository codiDiaryRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public ResponseEntity<Response<Object>> createCodiDiary(CreateCodiDiaryInput createCodiDiaryInput) {
        // 1. 값 형식 체크
        if (createCodiDiaryInput == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(NO_VALUES));
        if (!ValidationCheck.isValid(createCodiDiaryInput.getThumbnail()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(NO_VALUES));

        // 2. 코디일기 생성
        CodiDiary codiDiary;
        try {
            User loginUser = jwtService.getUser();
            if (loginUser == null)  {
                log.error("[GET]/diaries NOT FOUND LOGIN USER error");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<>(NOT_FOUND_USER));
            }
            codiDiary = CodiDiary.builder()
                    .user(loginUser)
                    .date(createCodiDiaryInput.getDate())
                    .thumbnail(createCodiDiaryInput.getThumbnail())
                    .content(createCodiDiaryInput.getContent())
                    .build();

            codiDiaryRepository.save(codiDiary);

        } catch (Exception e) {
            log.error("[diaries/post] database error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(DATABASE_ERROR));
        }

        // 3. 결과 return
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response<>(null, CREATED_CODI));
    }


    @Override
    public ResponseEntity<Response<SelectCodiDiaryOutput>> selectCodiDiary(int codiDiaryId) {
        // 1. 값 형식 체크
        if (!ValidationCheck.isValidId(codiDiaryId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(BAD_ID_VALUE));
        }
        // 2. 코디일기 조회
        CodiDiary codiDiary;
        SelectCodiDiaryOutput selectCodiDiaryOutput;
        try {
            codiDiary = codiDiaryRepository.findById(codiDiaryId).orElse(null);
            if(codiDiary==null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<>(NOT_FOUND_CODI));
        } catch (Exception e) {
            log.error("[diaries/get] database error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(DATABASE_ERROR));
        }

        // 최종 출력값 정리
        selectCodiDiaryOutput = SelectCodiDiaryOutput.builder()
                .codiDiaryId(codiDiary.getId())
                .userId(codiDiary.getUser().getId())
                .codiDiaryThumbnail(codiDiary.getThumbnail())
                .codiDiaryDate(codiDiary.getDate())
                .codiDiaryContent(codiDiary.getContent())
                .codiDiaryCreatedAt(codiDiary.getCreatedAt())
                .codiDiaryUpdatedAt(codiDiary.getUpdatedAt())
                .build();

        // 3. 결과 return
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response<>(selectCodiDiaryOutput, SUCCESS_SELECT_CODI));
    }

    @Override
    public ResponseEntity<PageResponse<SelectCodiDiaryOutput>> selectCodiDiaryList(SelectCodiDiaryInput selectCodiDiaryInput) {
        // 1. 값 형식 체크
        if (selectCodiDiaryInput == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PageResponse<>(NO_VALUES));
        if (!ValidationCheck.isValidPage(selectCodiDiaryInput.getPage())
                || !ValidationCheck.isValidId(selectCodiDiaryInput.getSize()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PageResponse<>(NO_VALUES));

        // 2. 코디일기 조회
        Pageable pageable = PageRequest.of(selectCodiDiaryInput.getPage() - 1, selectCodiDiaryInput.getSize());
        Page<SelectCodiDiaryOutput> selectCodiDiaryOutputs;
        try {
            selectCodiDiaryOutputs = codiDiaryRepository.findByDynamicQuery(selectCodiDiaryInput, pageable);
        } catch (Exception e) {
            log.error("[diaries/get] database error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PageResponse<>(DATABASE_ERROR));
        }

        // 3. 결과 return
        return ResponseEntity.status(HttpStatus.OK)
                .body(new PageResponse<>(selectCodiDiaryOutputs, SUCCESS_SELECT_CODI));
    }

    @Override
    @Transactional
    public ResponseEntity<Response<Object>> updateCodiDiary(UpdateCodiDiaryInput updateCodiDiaryInput, int codiDiaryId) {
        try {
            // 1. 코디일기 조회
            CodiDiary codiDiary = codiDiaryRepository.findById(codiDiaryId).orElse(null);

            // 2. 코디일기 수정
            if (codiDiary == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<>(BAD_ID_VALUE));
            if (StringUtils.isNoneBlank(updateCodiDiaryInput.getContent()))
                codiDiary.setContent(updateCodiDiaryInput.getContent());
            codiDiaryRepository.save(codiDiary);
        } catch (Exception e) {
            log.error("[diaries/patch] database error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(DATABASE_ERROR));
        }
        // 3. 결과 return
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response<>(null, SUCCESS_UPDATE_CODI));
    }

    @Override
    @Transactional
    public ResponseEntity<Response<Object>> deleteCodiDiary(int codiDiaryId) {
        try {
            // 1. 코디일기 조회
            CodiDiary codiDiary = codiDiaryRepository.findById(codiDiaryId).orElse(null);

            // 2. 코디일기 삭제
            if (codiDiary == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<>(BAD_ID_VALUE));

            codiDiaryRepository.delete(codiDiary);

        } catch (Exception e) {
            log.error("[diaries/delete] database error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(DATABASE_ERROR));
        }

        // 3. 결과 return
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response<>(null, SUCCESS_DELETE_CODI));
    }

}
