package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.dto.BankAccountDto;
import com.konkuk.moneymate.activities.dto.TransactionDto;
import com.konkuk.moneymate.activities.service.BankAccountService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.konkuk.moneymate.auth.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h3>BankAccountController : 계좌 api 관리 클래스</h3>
 * <li><b> POST /account/register : 계좌 등록 </b></li>
 * <li><b> GET /asset/get : 본인의 계좌 조회 </b></li>
 * <li><b> POST /account/get-transaction : 본인의 거래내역 조회</b></li>
 * <li><b> POST /account/delete : 계좌 삭제</b></li>
 */
@RequiredArgsConstructor
@RestController
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final JwtService jwtService;

    @PostMapping("/account/register")
    public ResponseEntity<?> registerAccount(@RequestBody Map<String,Object> body, HttpServletRequest request){
        String accountNumber = body.get("accountNumber").toString();
        String accountName = body.get("accountName").toString();
        String accountType = body.get("accountType").toString();
        String accountBank = body.get("accountBank").toString();

        BankAccountDto accountDto = new BankAccountDto(accountName, accountNumber, accountBank, accountType);
        // jwt에서 userUid 가져오기
        String userUid = jwtService.getUserUid(request);

        try{
            bankAccountService.registerAccount(accountDto, userUid);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            e.getMessage()));
        }catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.getReasonPhrase(),
                            e.getMessage()));
        }

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.ACCOUNT_REGISTER_SUCCESS.getMessage(), accountDto));
    }

    @GetMapping("/account/get-list")
    public ResponseEntity<?> getAccountList(HttpServletRequest request){
        String userUid = jwtService.getUserUid(request);

        List<BankAccountDto> bankAccountList = bankAccountService.getAccountList(userUid);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("account", bankAccountList);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.ACCOUNT_QUERY_SUCCESS.getMessage(), responseData));
    }

    @PostMapping("/account/get-transaction")
    public ResponseEntity<?> getTransaction(@RequestBody Map<String,Object> body, HttpServletRequest request){
        String accountUid = body.get("accountUid").toString();
        LocalDate startDate, endDate;
        try{
            startDate = LocalDate.parse(body.get("startDate").toString());
            endDate = LocalDate.parse(body.get("endDate").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            ApiResponseMessage.WRONG_FORMAT.getMessage()));
        }

        String userUid= jwtService.getUserUid(request);
        List<TransactionDto> transactionList;
        try{
            transactionList = bankAccountService.getTransactionList(accountUid, userUid, startDate, endDate);
        }
        catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(HttpStatus.FORBIDDEN.getReasonPhrase(),
                            e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.getReasonPhrase(),
                            e.getMessage()));
        }
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("transaction", transactionList);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.TRANSACTION_QUERY_SUCCESS.getMessage(), responseData));
    }

    @PostMapping("/account/delete")
    public ResponseEntity<?> deleteAccount(@RequestBody Map<String,Object> body, HttpServletRequest request){
        String accountUid = body.get("accountUid").toString();
        String userUid = jwtService.getUserUid(request);
        try {
            bankAccountService.delete(userUid, accountUid);
        }catch(IllegalAccessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(HttpStatus.FORBIDDEN.getReasonPhrase(),
                            e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.getReasonPhrase(),
                            e.getMessage()));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.ACCOUNT_DELETE_SUCCESS.getMessage()));
    }
}
