/** Models a folder. */
export type ProtelisFolder = {
  // path: string | null,
  name: string,
  content: Set<ProtelisFile>
};

/** Models a file. */
export type ProtelisSourceFile = {
  // path: string | null,
  name: string
  content: string
};

/** Models a file or a folder. */
export type ProtelisFile = ProtelisFolder | ProtelisSourceFile;
