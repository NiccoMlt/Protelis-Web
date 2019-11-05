export interface ProtelisFolder extends ProtelisFile {
  name: string
  content: Array<ProtelisFile>
}

export interface ProtelisSourceFile extends ProtelisFile {
  name: string
  content: string
}

export interface ProtelisFile {
  name: string
  content: string | Array<ProtelisFile>
}
