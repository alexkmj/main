package seedu.address.logic.parser.arguments;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Basic enum for DeleteArgument.
 */
public enum DeleteArgument {
    TARGET_CODE('t', "targetCode") {
        /**
         * Parses value into code.
         *
         * @param value target code in string
         * @return {@code Code} of the targeted module cast into a generic
         * {@code Object}
         * @throws ParseException Thrown when code cannot be parsed
         */
        @Override
        public Object getValue(String value) throws ParseException {
            return ParserUtil.parseCode(value);
        }
    },
    TARGET_YEAR('e', "targetYear") {
        /**
         * Parses value into year.
         *
         * @param value target year in string
         * @return {@code Year} cast into {@code Object}
         * @throws ParseException Thrown when year cannot be parsed
         */
        @Override
        public Object getValue(String value) throws ParseException {
            return ParserUtil.parseYear(value);
        }
    },
    TARGET_SEMESTER('z', "targetSemester") {
        /**
         * Parses value into semester.
         *
         * @param value target semester in string
         * @return {@code Semester} cast into {@code Object}
         * @throws ParseException Thrown when semester cannot be parsed
         */
        @Override
        public Object getValue(String value) throws ParseException {
            return ParserUtil.parseSemester(value);
        }
    };

    /**
     * A character that represents the name for the particular name-value pair.
     */
    private char shortName;

    /**
     * A string that represents the name for a the particular name-value pair.
     */
    private String longName;

    /**
     * Constructor that takes in the short name and long name.
     *
     * @param shortName short name of the name of a name-value pair
     * @param longName long name of the name of a name-value pair
     */
    DeleteArgument(char shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    /**
     * Returns the short name of a name-value pair. It is a prefix and a
     * character representing the name.
     *
     * @return short name of the name-value pair
     */
    public String getShortName() {
        return ParserUtil.NAME_PREFIX_SHORT + shortName;
    }

    /**
     * Returns the long name of a name-value pair. It is a long prefix and a
     * string representing the name.
     *
     * @return long name of the name-value pair
     */
    public String getLongName() {
        return ParserUtil.NAME_PREFIX_LONG + longName;
    }

    /**
     * Returns null.
     * <p>
     * Overwritten by the specific variable that will return the object
     * associated to it.
     */
    public Object getValue(String value) throws ParseException {
        return null;
    }
}
