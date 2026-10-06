package com.phungvanlong.booking_hotel.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiActionContext {
    private static final ThreadLocal<AiActionContext> CONTEXT = ThreadLocal.withInitial(AiActionContext::new);

    private String actionType;
    private Object actionPayload;

    public static AiActionContext get() {
        return CONTEXT.get();
    }

    public static void set(String actionType, Object actionPayload) {
        AiActionContext context = CONTEXT.get();
        context.setActionType(actionType);
        context.setActionPayload(actionPayload);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
