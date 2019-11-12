// TODO: refactor folder from a different type to a part of the name

export interface ProtelisFolder extends ProtelisFile {
  name: string
  content: Set<ProtelisFile>
}

export interface ProtelisSourceFile extends ProtelisFile {
  name: string
  content: string
}

export interface ProtelisFile {
  name: string
  content: string | Set<ProtelisFile>
}
