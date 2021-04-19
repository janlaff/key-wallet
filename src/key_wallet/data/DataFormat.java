package key_wallet.data;

import java.util.ArrayList;
import java.util.List;

public interface DataFormat {
    class Data {
        public List<Credential> credentials = new ArrayList<>();
    }

    Data decode(byte[] encodedData) throws DataFormatException;
    byte[] encode(Data data);
}
