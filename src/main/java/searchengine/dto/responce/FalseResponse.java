package searchengine.dto.responce;

import lombok.Value;

@Value
public class FalseResponse {
    boolean result;
    String error;
}
