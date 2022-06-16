package mk.ukim.finki.petshop.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import mk.ukim.finki.petshop.model.ChargeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {
    @Value("sk_test_51Iz1azA9T6OJb1xwJqiuFKDUgTn6k5BozdsJSoNFVXdSKwgzAu2x14PKguqMN5WgKu4FzILPCtRFDXxBoqFm5bi900pqR9ADvG")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
    public Charge charge(ChargeRequest chargeRequest)
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, CardException, APIException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("description", chargeRequest.getDescription());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return Charge.create(chargeParams);
    }
}
