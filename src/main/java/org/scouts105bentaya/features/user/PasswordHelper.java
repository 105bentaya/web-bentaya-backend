package org.scouts105bentaya.features.user;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;

public class PasswordHelper {
    private static final PasswordGenerator generator = new PasswordGenerator();

    public static String generatePassword() {
        CharacterRule lowerCase = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "ERROR_AT_PASSWORD_LOWER";
            }

            public String getCharacters() {
                return "abcdefghijkmnopqrstuvwxyz";
            }
        }, 2);
        CharacterRule upperCase = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "ERROR_AT_PASSWORD_UPPER";
            }

            public String getCharacters() {
                return "ABCDEFGHJKLMNPQRSTUVWXYZ";
            }
        }, 2);
        CharacterRule digit = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "ERROR_AT_PASSWORD_DIGIT";
            }

            public String getCharacters() {
                return "123456789";
            }
        }, 2);
        CharacterRule special = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "ERROR_AT_PASSWORD_SPECIAL";
            }

            public String getCharacters() {
                return "!_-;$%&/()";
            }
        }, 2);
        return generator.generatePassword(10, lowerCase, upperCase, digit, special);
    }
}
