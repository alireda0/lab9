package verifier;

import model.Board;
import model.Duplicate;
import model.VerificationResult;
import model.VerificationStatus;
import validator.BoxValidator;
import validator.ColumnValidator;
import validator.RowValidator;

import java.util.ArrayList;
import java.util.List;

public class BoardVerifier {

    private final RowValidator rowValidator = new RowValidator();
    private final ColumnValidator colValidator = new ColumnValidator();
    private final BoxValidator boxValidator = new BoxValidator();

    public VerificationResult verify(Board board) {
        List<Duplicate> all = new ArrayList<>();

        all.addAll(rowValidator.validate(board));
        all.addAll(colValidator.validate(board));
        all.addAll(boxValidator.validate(board));

        if (!all.isEmpty()) {
            return new VerificationResult(VerificationStatus.INVALID, all);
        }

        if (board.hasZero()) {
            return new VerificationResult(VerificationStatus.INCOMPLETE, all);
        }

        return new VerificationResult(VerificationStatus.VALID, all);
    }
}
