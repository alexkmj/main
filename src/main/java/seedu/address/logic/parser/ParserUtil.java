package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credit;
import seedu.address.model.module.Grade;
import seedu.address.model.module.Semester;
import seedu.address.model.module.Year;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

//@@author alexkmj
/**
 * Contains utility methods used for parsing strings in the various
 * Parser classes.
 */
public class ParserUtil {
    /**
     * Message that informs that the command is in a wrong format.
     */
    public static final String MESSAGE_INVALID_FORMAT = "Invalid format";

    /**
     * TODO: Remove legacy code.
     */
    public static final String MESSAGE_INVALID_INDEX = "Invalid index";

    /**
     * Message that informs that the target code is required.
     */
    public static final String MESSAGE_TARGET_CODE_REQUIRED = "Target code"
            + " required.";

    /**
     * Message that informs that target year has to be specified if and only if
     * semester is specified.
     */
    public static final String MESSAGE_YEAR_AND_SEMESTER_XOR_NULL = "Year can"
            + " only be specified if and only if semester is also specifed.";

    /**
     * Prefix used for short name.
     */
    public static final String NAME_PREFIX_SHORT = "-";

    /**
     * Prefix used for long name.
     */
    public static final String NAME_PREFIX_LONG = "--";

    /**
     * Tokenize arguments in a string into an argument array.
     *
     * @param args non-null string that contains the arguments
     * @return tokenized argument array
     */
    public static String[] tokenize(String args) {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        return trimmedArgs.split("\\s+");
    }

    /**
     * Size of {@code args} equals to the size of {@code size}.
     * <p>
     * Throws {@code ParseException} when size of {@code args} is not equal to
     * {@code size}.
     *
     * @param args argument array to validate
     * @param size the size that the argument array should have
     * @throws ParseException when the number of arguments is not equal to
     * {@code size}.
     */
    public static void argsWithBounds(Object[] args, int size)
            throws ParseException {
        requireNonNull(args);
        argsWithBounds(args, size, size);
    }

    /**
     * Size of {@code args} is between {@code min} and {@code max}.
     * <p>
     * Throws {@code ParseException} when size of {@code args} is not between
     * {@code min} and {@code max}.
     *
     * @param args argument array to validate
     * @param min the minimum size allowed for {@code args}
     * @param max the maximum size allowed for {@code args}
     * @throws ParseException when the number of arguments is not equal between
     * {@code min} and {@code max}.
     */
    public static void argsWithBounds(Object[] args, int min, int max)
            throws ParseException {
        requireNonNull(args);

        if (args.length < min || args.length > max) {
            throw new ParseException("Invalid number of arguments!"
                    + " Number of arguments should be more than or equal to "
                    + min
                    + " and less than or equal to "
                    + max);
        }
    }

    /**
     * Size of {@code args} is in {@code allowedSize}.
     * <p>
     * Throws {@code ParseException} when size of {@code args} is not in
     * {@code allowedSize}.
     *
     * @param args argument array to validate
     * @param allowedSize set containing size that {@code args} is allowed to
     * have
     * @throws ParseException when the number of arguments is not in
     * {@code allowedSize}
     */
    public static void argsWithBounds(Object[] args,
            Set<Integer> allowedSize) throws ParseException {
        requireNonNull(args);

        if (!allowedSize.contains(args.length)) {
            String allowedNumOfArgs = allowedSize.stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));

            throw parseException("Invalid number of arguments! "
                    + "Number of arguments should be "
                    + allowedNumOfArgs);
        }
    }

    /**
     * All arguments in {@code args} conforms to the name-value pair format.
     * <p>
     * For all of the arguments in the argument array, odd arguments must be a
     * name and even arguments must be a value. Throws {@code ParseException}
     * when argument is not in name value pair format.
     * <p>
     * <b>Valid:</b> -name1 value1 -name2 value2
     * <p>
     * <b>Invalid:</b> -name1 -name2 value2
     * <p>
     * <b>Invalid:</b> -name1 value1 value2
     *
     * @param args argument array that contains the name-value pair
     * @param errorMsg error message shown when ParseException is thrown
     * @throws ParseException thrown when argument array does not conform to
     * name-value pair format
     */
    public static void argsAreNameValuePair(String[] args, String errorMsg)
            throws ParseException {
        boolean invalidFormat = IntStream.range(0, args.length)
                .mapToObj(index -> {
                    boolean isEven = index % 2 == 0;
                    boolean isName = isName(args[index]);
                    return isEven == isName;
                })
                .anyMatch(booleanValue -> !booleanValue);

        if (invalidFormat) {
            throw parseException(errorMsg);
        }
    }

    /**
     * Returns true if argument is a name.
     * <p>
     * {@code argument} is a name if it starts with {@code NAME_PREFIX_SHORT}
     * or {@code NAME_PREFIX_LONG}.
     *
     * @param argument argument to be checked
     * @return true if argument is a name.
     */
    private static boolean isName(String argument) {
        return argument.startsWith(NAME_PREFIX_SHORT)
                || argument.startsWith(NAME_PREFIX_LONG);
    }

    /**
     * Argument array does not contain the same name twice and all names are
     * legal.
     *
     * @param args array of name-value pair arguments
     * @param nameToArgMap map that maps {@code T} to string which is the name
     * @param errorMsg message shown when {@code ParseException} is
     * thrown
     * @param <T> the argument enum
     * @throws ParseException thrown when there are duplicate or illegal name.
     */
    public static <T> void validateName(String[] args,
            Map<String, T> nameToArgMap, String errorMsg)
            throws ParseException {
        List<T> nameArray = IntStream.range(0, args.length)
                .filter(index -> index % 2 == 0)
                .mapToObj(index -> nameToArgMap.get(args[index]))
                .collect(Collectors.toList());

        boolean illegalNameExist = nameArray.stream()
                .anyMatch(Objects::isNull);

        if (illegalNameExist) {
            throw parseException(errorMsg);
        }

        Set<T> nameSet = new HashSet<>(nameArray);

        if (nameArray.size() != nameSet.size()) {
            throw parseException(errorMsg);
        }
    }

    /**
     * Target code is not null.
     *
     * @param targetCode {@code Code} that identifies the target {@code Module}
     * @param errorMsg message shown when {@code ParseException} is thrown
     * @throws ParseException thrown when target code is null
     */
    public static void targetCodeNotNull(Object targetCode, String errorMsg)
            throws ParseException {
        if (targetCode == null) {
            throw parseException(errorMsg);
        }
    }

    /**
     * Target year and target semester cannot be exclusively null.
     *
     * @throws ParseException thrown when target year and target semester is
     * exclusively null
     */
    public static void targetYearNullIffTargetSemesterNull(Object targetYear,
            Object targetSemester, String errorMsg) throws ParseException {
        if (targetYear == null ^ targetSemester == null) {
            throw parseException(errorMsg);
        }
    }

    /**
     * Creates parse exception with the error message.
     *
     * @param errorMsg error messge for the exception
     * @return {@code ParseException} with {@code errorMsg} as the message
     */
    public static ParseException parseException(String errorMsg) {
        String messageError = String.format(errorMsg);
        return new ParseException(messageError);
    }

    /**
     * Parses a {@code String code} into a {@code Code}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException thrown when the given {@code code} is invalid.
     */
    public static Code parseCode(String args) throws ParseException {
        requireNonNull(args);
        String trimmedCode = args.trim();
        trimmedCode = trimmedCode.toUpperCase();

        if (!Code.isValidCode(trimmedCode)) {
            throw new ParseException(Code.MESSAGE_CODE_CONSTRAINTS);
        }
        return new Code(trimmedCode);
    }

    /**
     * Parses a {@code String year} into a {@code Year}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException thrown when the given {@code year} is invalid.
     */
    public static Year parseYear(String args) throws ParseException {
        requireNonNull(args);
        String trimmedYear = args.trim();
        if (!Year.isValidYear(trimmedYear)) {
            throw new ParseException(Year.MESSAGE_YEAR_CONSTRAINTS);
        }
        return new Year(trimmedYear);
    }

    /**
     * Parses a {@code String semester} into a {@code Semester}. Leading and
     * trailing whitespaces will be trimmed.
     *
     * @throws ParseException thrown when the given {@code semester} is invalid.
     */
    public static Semester parseSemester(String args) throws ParseException {
        requireNonNull(args);
        String trimmedSemester = args.trim();
        if (!Semester.isValidSemester(trimmedSemester)) {
            throw new ParseException(Semester.MESSAGE_SEMESTER_CONSTRAINTS);
        }
        return new Semester(trimmedSemester);
    }

    /**
     * Parses a {@code String credit} into a {@code Credit}. Leading and
     * trailing whitespaces will be trimmed.
     *
     * @throws ParseException thrown when the given {@code credit} is invalid.
     */
    public static Credit parseCredit(String args) throws ParseException {
        requireNonNull(args);
        String trimmedCredit = args.trim();
        int intCredit = Integer.parseInt(trimmedCredit);
        if (!Credit.isValidCredit(intCredit)) {
            throw new ParseException(Credit.MESSAGE_CREDIT_CONSTRAINTS);
        }
        return new Credit(intCredit);
    }

    /**
     * Parses a {@code String grade} into a {@code Grade}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException thrown when the given {@code grade} is invalid.
     */
    public static Grade parseGrade(String args) throws ParseException {
        requireNonNull(args);
        String trimmedGrade = args.trim();
        if (!Grade.isValidGrade(trimmedGrade)) {
            throw new ParseException(Grade.MESSAGE_GRADE_CONSTRAINTS);
        }
        return new Grade(trimmedGrade);
    }

    /**
     * TODO: Remove legacy code.
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException thrown when the specified index is invalid
     * (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * TODO: Remove legacy code.
     * Parses a {@code String name} into a {@code Name}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * TODO: Remove legacy code.
     * Parses a {@code String phone} into a {@code Phone}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_PHONE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * TODO: Remove legacy code.
     * Parses a {@code String address} into an {@code Address}. Leading and
     * trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_ADDRESS_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * TODO: Remove legacy code.
     * Parses a {@code String email} into an {@code Email}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_EMAIL_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * TODO: Remove legacy code.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_TAG_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * TODO: Remove legacy code.
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags)
            throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
