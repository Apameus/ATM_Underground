package underground.atm.serializers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import underground.atm.data.CreditCard;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCreditCardSerializerUnitTest {
    private final CreditCardSerializer serializer = new CreditCardSerializer();
    private final CreditCard VALID_Credit_CARD = new CreditCard(1111,"11",100);
    private final String VALID_LINE = "1111" + "," + "11" + "," + "100";

    @Test
    @DisplayName("Parse Card")
    void parseCard() {
        assertThat(serializer.parse(VALID_LINE)).isEqualTo(VALID_Credit_CARD);
    }

    @Test
    @DisplayName("Serialize Card")
    void serializeCard() {
        assertThat(serializer.serialize(VALID_Credit_CARD)).isEqualTo(VALID_LINE);
    }

}