package seedu.address.logic.parser.arguments;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Basic enum for AddArgument.
 */
public enum AddArgument {
    NEW_CODE('m', "code") {
        /**
         * Parses value into code.
         *
         * @param value new code in string
         * @return {@code Code} cast into {@code Object}
         * @throws ParseException Thrown when code cannot be parsed
         */
        @Override
        public Object getValue(String value) throws ParseException {
            return ParserUtil.parseCode(value);
        }
    },
    NEW_YEAR('y', "year") {
        /**
         * Parses value into year.
         *
         * @param value new year in string
         * @return {@code Year} cast into {@code Object}
         * @throws ParseException Thrown when year cannot be parsed
         */
        @Override
        public Object getValue(String value) throws ParseException {
            return ParserUtil.parseYear(value);
        }
    },
    NEW_SEMESTER('s', "semester") {
        /**
         * Parses value into semester.
         *
         * @param value new semester in string
         * @return {@code Semester} cast into {@code Object}
         * @throws ParseException Thrown when semester cannot be parsed
         */
        @Override
        public Object getValue(String value) throws ParseException {
            return ParserUtil.parseSemester(value);
        }
    },
    NEW_CREDIT('c', "credit") {
        /**
         * Parses value into credit.
         *
         * @param value new credit in string
         * @return {@code Credit} cast into {@code Object}
         * @throws ParseException Thrown when credit cannot be parsed
         */
        @Override
        public Object getValue(String value) throws ParseException {
            return ParserUtil.parseCredit(value);
        }
    },
    NEW_GRADE('g', "grade") {
        /**
         * Parses value into grade.
         *
         * @param value new grade in string
         * @return {@code Grade} cast into {@code Object}
         * @throws ParseException Thrown when grade cannot be parsed
         */
        @Override
        public Object getValue(String value) throws ParseException {
            return ParserUtil.parseGrade(value);
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
    AddArgument(char shortName, String longName) {
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
