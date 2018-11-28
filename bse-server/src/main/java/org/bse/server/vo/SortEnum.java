package org.bse.server.vo;

public enum SortEnum {

	/**
	 * 升序
	 */
	asc {
		@Override
		public int value() {

			return 1;
		}


		@Override
		public String stringValue() {

			return "asc";
		}
	},
	/**
	 * 降序
	 */
	desc {
		@Override
		public int value() {

			return 2;
		}


		@Override
		public String stringValue() {

			return "desc";
		}
	};
	public abstract int value();


	public abstract String stringValue();
}
