package gtm.hibernateapp;

public enum TeacherCondition {
    PRESENT{
        @Override
        public String toString() {
            return "Present";
        }
    },
    DELEGATION{
        @Override
        public String toString() {
            return "Delegation";
        }
    },
    ILL{
        @Override
        public String toString() {
            return "Ill";
        }
    },
    ABSENT{
        @Override
        public String toString() {
            return "Absent";
        }
    }

}