package com.paylike;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.paylike.exceptions.PaylikeException;
import com.paylike.models.*;
import com.paylike.models.requests.*;
import com.paylike.models.responses.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by jankjr on 17/11/2016.
 */
public class Paylike {
  public static String apiBase = "https://api.paylike.io/";

  public static<T> List<T> jsonArrayToTypedList(Class<T> cls, JsonArray arr){
    Gson g = new Gson();
    ArrayList<T> result = new ArrayList<T>();
    for(JsonElement elemen : arr){
      result.add(g.fromJson(elemen, cls));
    }
    return result;
  }

  public static String pagination(int limit, String before, String after){
    if(before == null && after == null){
      return String.format("limit=%s", limit);
    }
    if(after == null){
      return String.format("limit=%s&before=%s", limit, before);
    }
    return String.format("limit=%s&before=%s&after=%s", limit, before, after);
  }

  public static String path(String path, Object ... args){
    return String.format("%s%s", apiBase, String.format(path, args));
  }

  public static CreateAppResponse createApp(String name) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(CreateAppResponse.class, "POST", path("apps"), new CreateAppData(name), null);
  }

  public static Identity currentApp(String base64EncodedAuthorization) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(CurrentAppResponse.class, "GET", path("me"), null, base64EncodedAuthorization).identity;
  }

  public static CreateMerchantResponse createMerchant(String base64EncodedAuthorization, MerchantData merchantDefinition) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(CreateMerchantResponse.class, "POST", path("merchants"), merchantDefinition, base64EncodedAuthorization);
  }

  public static void updateMerchant(String base64EncodedAuthorization, String merchantId, UpdateMerchantData updateMerchantDefinition) throws IOException, PaylikeException {
    MinimalistPaylikeClient.call(Void.class, "PUT", path("merchants/%s", merchantId), updateMerchantDefinition, base64EncodedAuthorization);
  }

  public static List<Merchant> fetchAllMerchants(String base64EncodedAuthorization, String appId, int limit, String before, String after) throws IOException, PaylikeException {
    JsonArray res = MinimalistPaylikeClient.call(JsonArray.class, "GET", path("identities/%s/merchants?%s", appId, pagination(limit, before, after)), null, base64EncodedAuthorization);
    return jsonArrayToTypedList(Merchant.class, res);
  }

  public static Merchant fetchMerchant(String base64EncodedAuthorization, String merchantId) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(Merchant.class, "GET", path("merchants/%s", merchantId), null, base64EncodedAuthorization);
  }

  public static InviteUserResponse inviteUser(String base64EncodedAuthorization, String merchantId, InviteUserData invitationData) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(InviteUserResponse.class, "POST", path("merchants/%s/users", merchantId), invitationData, base64EncodedAuthorization);
  }

  public static void revokeUser(String base64EncodedAuthorization, String merchantId, String userId) throws IOException, PaylikeException {
    MinimalistPaylikeClient.call(Void.class, "DELETE", path("merchants/%s/users/%s", merchantId, userId), null, base64EncodedAuthorization);
  }

  public static List<User> fetchAllUsers(String base64EncodedAuthorization, String merchantId, int limit, String before, String after) throws IOException, PaylikeException {
    JsonArray arr = MinimalistPaylikeClient.call(JsonArray.class, "GET", path("merchants/%s/users?%s", merchantId, pagination(limit, before, after)), null, base64EncodedAuthorization);
    return jsonArrayToTypedList(User.class, arr);
  }
  public static void addApp(String base64EncodedAuthorization, String merchantId, String appId) throws IOException, PaylikeException {
    MinimalistPaylikeClient.call(Void.class, "POST", path("merchants/%s/apps", merchantId), new AddAppData(appId), base64EncodedAuthorization);
  }

  public static void revokeApp(String base64EncodedAuthorization, String merchantId, String appId) throws IOException, PaylikeException {
    MinimalistPaylikeClient.call(Void.class, "DELETE", path("merchants/%s/apps/%s", merchantId, appId), null, base64EncodedAuthorization);
  }

  public static List<Line> fetchAllMerchantLines(String base64EncodedAuthorization, String merchantId, int limit, String before, String after) throws IOException, PaylikeException {
    JsonArray arr = MinimalistPaylikeClient.call(JsonArray.class, "GET", path("merchants/%s/lines?%s", merchantId, pagination(limit, before, after)), null, base64EncodedAuthorization);
    return jsonArrayToTypedList(Line.class, arr);
  }

  public static String createTransactionFromPreviousTransaction(String base64EncodedAuthorization, String merchantId, CreateTransactionFromPreviusTransactionData data) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(TransactionResponse.class, "POST", path("merchants/%s/transactions", merchantId), data, base64EncodedAuthorization).transaction.id;
  }

  public static String createFromSavedCard(String base64EncodedAuthorization, String merchantId, String cardId, String currency, String descriptor, int amount, Map custom) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(TransactionResponse.class, "POST", path("merchants/%s/transactions", merchantId), new CreateTransactionFromSavedCardInput(cardId, currency, descriptor, amount, custom), base64EncodedAuthorization).transaction.id;
  }

  public static Transaction refundTransaction(String base64EncodedAuthorization, String transactionId, RefundData data) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(RefundResponse.class, "POST", path("transactions/%s/refunds", transactionId), data, base64EncodedAuthorization).transaction;
  }

  public static Transaction voidTransaction(String base64EncodedAuthorization, String transactionId, VoidData data) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(TransactionResponse.class, "POST", path("transactions/%s/voids", transactionId), data, base64EncodedAuthorization).transaction;
  }

  public static List<Transaction> fetchAllTransactions(String base64EncodedAuthorization, String merchantId, int limit, String before, String after) throws IOException, PaylikeException {
    JsonArray res = MinimalistPaylikeClient.call(JsonArray.class, "GET", path("merchants/%s/transactions?%s", merchantId, pagination(limit, before, after)), null, base64EncodedAuthorization);
    return jsonArrayToTypedList(Transaction.class, res);
  }

  public static Transaction fetchTransaction(String base64EncodedAuthorization, String transactionId) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(FetchTransactionResponse.class, "GET", path("transactions/%s", transactionId), null, base64EncodedAuthorization).transaction;
  }

  public static CardWithId saveCard(String base64EncodedAuthorization, String merchantId, String transactionId, String notes) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(SaveCardResponse.class, "POST", path("merchants/%s/cards", merchantId), new SaveCardData(transactionId, notes), base64EncodedAuthorization).card;
  }

  public static Transaction captureTransaction(String base64EncodedAuthorization, String transactionId, long amount, String currency, String descriptor) throws IOException, PaylikeException {
    return MinimalistPaylikeClient.call(TransactionResponse.class, "POST", path("transactions/%s/captures", transactionId), new CaptureTransactionData(amount, currency, descriptor), base64EncodedAuthorization).transaction;
  }
}