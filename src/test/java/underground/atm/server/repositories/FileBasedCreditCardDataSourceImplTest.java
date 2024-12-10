package underground.atm.server.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import underground.atm.common.data.CreditCard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileBasedCreditCardDataSourceImplTest { //TODO: Add tests


    @Test
    @DisplayName("Load cache from empty file")
    void loadCacheFromEmptyFile(@TempDir Path path) throws IOException {
        Path data = path.resolve("empty.data");
        Files.createFile(data);

        var dataSource = assertDoesNotThrow(() -> new FileBasedCreditCardDataSourceImpl(data));
        var repo = new Server_CreditCardRepositoryImpl(dataSource);

        assertThrows(NullPointerException.class, () -> repo.findCardBy(5000));
    }

    @Test
    @DisplayName("Load cache and update it")
    void loadCacheAndUpdateIt(@TempDir Path path) throws IOException {
        var data = path.resolve("Credits");
        var dataSource = new FileBasedCreditCardDataSourceImpl(data);

        HashMap<Integer, CreditCard> creditCards = new HashMap<>();
        CreditCard credit = new CreditCard(2004, "4", 100);
        creditCards.put(credit.id(), credit);

        dataSource.save(creditCards);

        var repo = new Server_CreditCardRepositoryImpl(dataSource);

        repo.updateAmount(credit.id(), 150);

        assertThat(repo.findCardBy(credit.id())).isEqualTo(credit.withAmount(150));
    }

    @Test
    @DisplayName("Save cache to file")
    void saveCacheToFile() {

    }
}