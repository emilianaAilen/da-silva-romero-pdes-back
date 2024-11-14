package pdes.unq.com.APC.dtos.mercadoLibre;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DescriptionItem {
    private String text;
    private String plain_text;
    private String last_updated;
    private String date_created;
    private Snapshot snapshot;

    @Data
    public static class Snapshot {
        private String url;
        private int width;
        private int height;
        private String status;
    }
}
