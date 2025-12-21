package model;

import java.util.List;

public class VerificationResult {
    private final VerificationStatus status;
    private final List<Duplicate> duplicates;

    public VerificationResult(VerificationStatus status, List<Duplicate> duplicates) {
        this.status = status;
        this.duplicates = duplicates;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public List<Duplicate> getDuplicates() {
        return duplicates;
    }

    public boolean isValid() {
        return status == VerificationStatus.VALID;
    }
}
