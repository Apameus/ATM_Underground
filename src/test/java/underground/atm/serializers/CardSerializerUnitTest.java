package underground.atm.serializers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import underground.atm.data.Card;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CardSerializerUnitTest {
    private final CardSerializer serializer = new CardSerializer();
    private final Card VALID_CARD = new Card(1111,11,100);
    private final String VALID_LINE = new String("1111" + "," + "11" + "," + "100");

    @Test
    @DisplayName("Parse Card")
    void parseCard() {
        assertThat(serializer.parse(VALID_LINE)).isEqualTo(VALID_CARD);
    }

    @Test
    @DisplayName("Serialize Card")
    void serializeCard() {
        assertThat(serializer.serialize(VALID_CARD)).isEqualTo(VALID_LINE);
    }

}