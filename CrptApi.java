import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {

    private final String apiUrl;
    private final Semaphore semaphore;

    public CrptApi(String apiUrl, TimeUnit timeUnit, int requestLimit) {
        this.apiUrl = apiUrl;
        this.semaphore = new Semaphore(requestLimit);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(timeUnit.toMillis(1));
                    semaphore.drainPermits();
                    semaphore.release(requestLimit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void createDocument(Document document, String signature) throws InterruptedException {
        semaphore.acquire();
        String jsonBody = buildJsonBody(document, signature);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                System.out.println(response.toString());
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    private String buildJsonBody(Document document, String signature) {
        return "{\"description\":{\"participantInn\":\"" + document.getParticipantInn() + "\"},\"doc_id\":\"" + document.getDocId() + "\",\"doc_status\":\"" + document.getDocStatus() + "\",\"doc_type\":\"LP_INTRODUCE_GOODS\",\"importRequest\":true,\"owner_inn\":\"" + document.getOwnerInn() + "\",\"participant_inn\":\"" + document.getParticipantInn() + "\",\"producer_inn\":\"" + document.getProducerInn() + "\",\"production_date\":\"" + document.getProductionDate() + "\",\"production_type\":\"" + document.getProductionType() + "\",\"products\":[{\"certificate_document\":\"" + document.getCertificateDocument() + "\",\"certificate_document_date\":\"" + document.getCertificateDocumentDate() + "\",\"certificate_document_number\":\"" + document.getCertificateDocumentNumber() + "\",\"owner_inn\":\"" + document.getOwnerInn() + "\",\"producer_inn\":\"" + document.getProducerInn() + "\",\"production_date\":\"" + document.getProductionDate() + "\",\"tnved_code\":\"" + document.getTnvedCode() + "\",\"uit_code\":\"" + document.getUitCode() + "\",\"uitu_code\":\"" + document.getUituCode() + "\"}],\"reg_date\":\"" + document.getRegDate() + "\",\"reg_number\":\"" + document.getRegNumber() + "\"}";
    }

    public static void main(String[] args) throws InterruptedException {
        String ApiUrl = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        CrptApi crptApi = new CrptApi(ApiUrl, TimeUnit.SECONDS, 10);

        Document document = new Document("1234567890",
                                        "doc123",
                                        "new",
                                        "LP_INTRODUCE_GOODS",
                                        true,
                                        "owner123",
                                        "participant123",
                                        "producer123",
                                        "2020-01-23",
                                        "type123",
                                        "cert123",
                                        "2020-01-23",
                                        "cert123",
                                        "tnved123", 
                                        "uit123", 
                                        "uitu123", 
                                        "2020-01-23");
        String signature = "sugnature";

        crptApi.createDocument(document, signature);
    }

    static class Document {
        private final String participantInn;
        private final String docId;
        private final String docStatus;
        private final String docType;
        private final boolean importRequest;
        private final String ownerInn;
        private final String producerInn;
        private final String productionDate;
        private final String productionType;
        private final String certificateDocument;
        private final String certificateDocumentDate;
        private final String certificateDocumentNumber;
        private final String tnvedCode;
        private final String uitCode;
        private final String uituCode;
        private final String regDate;
        private final String regNumber;

        public Document(String participantInn, String docId, String docStatus, String docType, boolean importRequest, String ownerInn, String producerInn, String productionDate, String productionType, String certificateDocument, String certificateDocumentDate, String certificateDocumentNumber, String tnvedCode, String uitCode, String uituCode, String regDate, String regNumber) {
            this.participantInn = participantInn;
            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.certificateDocument = certificateDocument;
            this.certificateDocumentDate = certificateDocumentDate;
            this.certificateDocumentNumber = certificateDocumentNumber;
            this.tnvedCode = tnvedCode;
            this.uitCode = uitCode;
            this.uituCode = uituCode;
            this.regDate = regDate;
            this.regNumber = regNumber;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public String getDocId() {
            return docId;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public String getDocType() {
            return docType;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public String getProductionDate() {
            return productionDate;
        }

        public String getProductionType() {
            return productionType;
        }

        public String getCertificateDocument() {
            return certificateDocument;
        }

        public String getCertificateDocumentDate() {
            return certificateDocumentDate;
        }

        public String getCertificateDocumentNumber() {
            return certificateDocumentNumber;
        }

        public String getTnvedCode() {
            return tnvedCode;
        }

        public String getUitCode() {
            return uitCode;
        }

        public String getUituCode() {
            return uituCode;
        }

        public String getRegDate() {
            return regDate;
        }

        public String getRegNumber() {
            return regNumber;
        }
    }
}
