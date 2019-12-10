import React from 'react';
import { withStyles } from '@material-ui/styles';
import { Theme, createStyles } from '@material-ui/core';
import { fade } from '@material-ui/core/styles';
import TreeItem, { TreeItemProps } from '@material-ui/lab/TreeItem';

/** The FileTreeItem view component gets contained file path from props. */
type FileTreeItemProps = TreeItemProps & {
  /** The full path of the file in the item */
  filePath: string;
};

/** Wrap Material-UI TreeItem with CSS-in-JS style using an Higher-Order-Component. */
const StyledTreeItem = withStyles(
  (theme: Theme) => createStyles({
    iconContainer: {
      '& .close': {
        opacity: 0.3,
      },
    },
    group: {
      marginLeft: 12,
      paddingLeft: 12,
      borderLeft: `1px dashed ${fade(theme.palette.text.primary, 0.4)}`,
    },
  }),
)(
  (props: TreeItemProps) => <TreeItem {...props} />,
);

export const FileTreeItem: React.FC<FileTreeItemProps> = (props: FileTreeItemProps) => {
  const {
    filePath,
    ...treeItemProps
  } = props;

  function handleClick(event: React.MouseEvent<HTMLDivElement>): void {
    event.preventDefault();
    // TODO
  }

  return (
    <div onContextMenu={handleClick} style={{ cursor: 'context-menu' }}>
      <StyledTreeItem {...treeItemProps} />
    </div>
  );
};

export default FileTreeItem;
